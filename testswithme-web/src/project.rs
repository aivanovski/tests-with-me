use std::collections::HashSet;

use crate::{
    api::client::{get_flows, get_groups, get_projects},
    flow::yaml_file_name,
    session::{Session, clear_session},
};
use leptos::{prelude::*, task::spawn_local};
use leptos_router::{
    NavigateOptions,
    components::A,
    hooks::{use_navigate, use_params_map},
};
use testswithme_api_rust::{FlowsItemDto, GroupItemDto, ProjectsItemDto};

#[derive(Clone, PartialEq)]
enum TreeEntryKind {
    Group,
    Flow,
}

#[derive(Clone, PartialEq)]
struct TreeEntry {
    id: String,
    name: String,
    depth: usize,
    kind: TreeEntryKind,
    ancestor_group_ids: Vec<String>,
}

#[component]
pub fn ProjectPage(session: RwSignal<Option<Session>>) -> impl IntoView {
    let params = use_params_map();
    let project_id = params.get_untracked().get("id").unwrap_or_default();
    let (error, set_error) = signal(None::<String>);
    let navigate = use_navigate();
    let active_session = session
        .get_untracked()
        .expect("ProjectPage requires an authenticated session");
    let project_id_for_navigation = project_id.clone();
    let project_id = Signal::derive(move || Some(project_id_for_navigation.clone()));
    let active_flow_id = Signal::derive(|| None);
    let navigation_token = active_session.token.clone();

    let logout = move |_| match clear_session() {
        Ok(()) => {
            session.set(None);
            navigate(
                "/login",
                NavigateOptions {
                    replace: true,
                    ..Default::default()
                },
            );
        }
        Err(logout_error) => set_error.set(Some(logout_error)),
    };

    view! {
        <main class="dashboard-page project-workspace-page">
            <header class="dashboard-header">
                <A href="/dashboard" attr:class="dashboard-brand">
                    <span class="brand-mark" aria-hidden="true">
                        <span></span>
                        <span></span>
                        <span></span>
                    </span>
                    <span>"TestsWithMe"</span>
                </A>

                <div class="dashboard-account">
                    <span>{active_session.user_name}</span>
                    <button class="dashboard-logout" type="button" on:click=logout>
                        "Log out"
                    </button>
                </div>
            </header>

            <Show when=move || error.get().is_some()>
                <p class="form-error workspace-global-error" role="alert">
                    {move || error.get().unwrap_or_default()}
                </p>
            </Show>

            <section class="project-workspace">
                <ProjectNavigation project_id token=navigation_token active_flow_id />
                <section
                    class="workspace-main workspace-main-empty"
                    aria-label="Flow content"
                ></section>
            </section>
        </main>
    }
}

#[component]
pub fn ProjectNavigation(
    project_id: Signal<Option<String>>,
    token: String,
    active_flow_id: Signal<Option<String>>,
) -> impl IntoView {
    let project_id = Memo::new(move |_| project_id.get());
    let (project, set_project) = signal(None::<ProjectsItemDto>);
    let (entries, set_entries) = signal(Vec::<TreeEntry>::new());
    let (open_groups, set_open_groups) = signal(HashSet::<String>::new());
    let (error, set_error) = signal(None::<String>);
    let (is_loading, set_is_loading) = signal(true);

    Effect::new(move |_| {
        let Some(requested_project_id) = project_id.get() else {
            return;
        };

        set_project.set(None);
        set_entries.set(Vec::new());
        set_error.set(None);
        set_is_loading.set(true);

        let token = token.clone();
        spawn_local(async move {
            let result = async {
                let projects = get_projects(&token).await?;
                let selected_project = projects
                    .projects
                    .into_iter()
                    .find(|project| project.id == requested_project_id)
                    .ok_or_else(|| "Project not found.".to_owned())?;
                let groups = get_groups(&token).await?;
                let flows = get_flows(&token).await?;
                let project_entries = build_tree(&selected_project, groups.groups, flows.flows);

                Ok::<_, String>((selected_project, project_entries))
            }
            .await;

            match result {
                Ok((selected_project, project_entries)) => {
                    set_open_groups.set(
                        project_entries
                            .iter()
                            .filter(|entry| entry.kind == TreeEntryKind::Group)
                            .map(|entry| entry.id.clone())
                            .collect(),
                    );
                    set_project.set(Some(selected_project));
                    set_entries.set(project_entries);
                }
                Err(project_error) => set_error.set(Some(project_error)),
            }
            set_is_loading.set(false);
        });
    });

    view! {
        <aside class="workspace-sidebar">
            <A href="/dashboard" attr:class="back-link workspace-back-link">
                <span aria-hidden="true">"←"</span>
                "All projects"
            </A>

            <div class="workspace-project-title">
                <p class="eyebrow">"Project flows"</p>
                <h1>
                    {move || {
                        project
                            .get()
                            .map(|project| project.name)
                            .unwrap_or_default()
                    }}
                </h1>
                <p>
                    {move || {
                        project
                            .get()
                            .map(|project| project.package_name)
                            .unwrap_or_default()
                    }}
                </p>
                <span class="flow-count">
                    {move || {
                        let flow_count = entries
                            .get()
                            .iter()
                            .filter(|entry| entry.kind == TreeEntryKind::Flow)
                            .count();
                        format!("{flow_count} flow{}", if flow_count == 1 { "" } else { "s" })
                    }}
                </span>
            </div>

            <Show when=move || error.get().is_some()>
                <p class="form-error dashboard-error" role="alert">
                    {move || error.get().unwrap_or_default()}
                </p>
            </Show>

            <Show
                when=move || !is_loading.get()
                fallback=|| {
                    view! {
                        <div class="workspace-navigation-state" role="status">
                            <span class="loader"></span>
                            <span>"Loading project flows..."</span>
                        </div>
                    }
                }
            >
                <Show
                    when=move || error.get().is_none() && !entries.get().is_empty()
                    fallback=move || {
                        error.get().is_none().then(|| {
                            view! {
                                <div class="workspace-navigation-state">
                                    <strong>"No flows yet"</strong>
                                    <span>"This project does not contain any flow files."</span>
                                </div>
                            }
                        })
                    }
                >
                    <div class="flow-tree" role="tree" aria-label="Project flow files">
                        <For
                            each=move || {
                                let open_groups = open_groups.get();
                                entries
                                    .get()
                                    .into_iter()
                                    .filter(|entry| {
                                        entry
                                            .ancestor_group_ids
                                            .iter()
                                            .all(|group_id| open_groups.contains(group_id))
                                    })
                                    .collect::<Vec<_>>()
                            }
                            key=|entry| {
                                let kind = match entry.kind {
                                    TreeEntryKind::Group => "group",
                                    TreeEntryKind::Flow => "flow",
                                };
                                format!("{kind}-{}", entry.id)
                            }
                            children=move |entry| {
                                    let indentation = format!(
                                        "--tree-indent: {}px; --connector-left: {}px",
                                        16 + entry.depth * 24,
                                        26 + entry.depth.saturating_sub(1) * 24,
                                    );
                                    let is_group = entry.kind == TreeEntryKind::Group;
                                    let is_nested = entry.depth > 0;
                                    let aria_level = entry.depth + 1;

                                    if is_group {
                                        let group_id = entry.id.clone();
                                        let group_id_for_state = group_id.clone();
                                        let is_open = Memo::new(move |_| {
                                            open_groups.get().contains(&group_id_for_state)
                                        });
                                        let toggle_group = move |_| {
                                            set_open_groups.update(|open_groups| {
                                                if !open_groups.remove(&group_id) {
                                                    open_groups.insert(group_id.clone());
                                                }
                                            });
                                        };

                                        view! {
                                            <button
                                                class="tree-entry tree-group"
                                                class:tree-nested=is_nested
                                                style=indentation
                                                type="button"
                                                role="treeitem"
                                                attr:aria-level=aria_level
                                                attr:aria-expanded=is_open
                                                on:click=toggle_group
                                            >
                                                <span class="tree-connector" aria-hidden="true"></span>
                                                <span
                                                    class="tree-chevron"
                                                    class:tree-chevron-open=is_open
                                                    aria-hidden="true"
                                                ></span>
                                                <span class="tree-icon" aria-hidden="true"></span>
                                                <span>{entry.name}</span>
                                            </button>
                                        }
                                            .into_any()
                                    } else {
                                        let flow_id = entry.id.clone();
                                        let flow_id_for_current = flow_id.clone();
                                        let flow_url = format!("/flow/{}", entry.id);
                                        let flow_class = if is_nested {
                                            "tree-entry tree-flow tree-nested"
                                        } else {
                                            "tree-entry tree-flow"
                                        };
                                        view! {
                                            <A
                                                href=flow_url
                                                attr:class=flow_class
                                                class:tree-flow-active=move || {
                                                    active_flow_id.get().as_deref()
                                                        == Some(flow_id.as_str())
                                                }
                                                attr:style=indentation
                                                attr:role="treeitem"
                                                attr:aria-current=move || {
                                                    (active_flow_id.get().as_deref()
                                                        == Some(flow_id_for_current.as_str()))
                                                        .then_some("page")
                                                }
                                            >
                                                <span class="tree-connector" aria-hidden="true"></span>
                                                <span class="tree-chevron-spacer" aria-hidden="true"></span>
                                                <span class="tree-icon" aria-hidden="true"></span>
                                                <span>{entry.name}</span>
                                            </A>
                                        }
                                            .into_any()
                                    }
                            }
                        />
                    </div>
                </Show>
            </Show>
        </aside>
    }
}

fn build_tree(
    project: &ProjectsItemDto,
    groups: Vec<GroupItemDto>,
    flows: Vec<FlowsItemDto>,
) -> Vec<TreeEntry> {
    let project_groups = groups
        .into_iter()
        .filter(|group| group.project_id == project.id)
        .collect::<Vec<_>>();
    let project_flows = flows
        .into_iter()
        .filter(|flow| flow.project_id == project.id)
        .collect::<Vec<_>>();
    let mut entries = Vec::new();
    let mut visited = HashSet::new();

    append_group_children(
        &project.root_group_id,
        0,
        &project_groups,
        &project_flows,
        &[],
        &mut visited,
        &mut entries,
    );

    entries
}

fn append_group_children(
    parent_id: &str,
    depth: usize,
    groups: &[GroupItemDto],
    flows: &[FlowsItemDto],
    ancestor_group_ids: &[String],
    visited: &mut HashSet<String>,
    entries: &mut Vec<TreeEntry>,
) {
    if !visited.insert(parent_id.to_owned()) {
        return;
    }

    let mut child_groups = groups
        .iter()
        .filter(|group| group.parent_id.as_deref() == Some(parent_id))
        .collect::<Vec<_>>();
    child_groups.sort_by_key(|group| group.name.to_lowercase());

    for group in child_groups {
        entries.push(TreeEntry {
            id: group.id.clone(),
            name: group.name.clone(),
            depth,
            kind: TreeEntryKind::Group,
            ancestor_group_ids: ancestor_group_ids.to_vec(),
        });
        let mut child_ancestor_ids = ancestor_group_ids.to_vec();
        child_ancestor_ids.push(group.id.clone());
        append_group_children(
            &group.id,
            depth + 1,
            groups,
            flows,
            &child_ancestor_ids,
            visited,
            entries,
        );
    }

    let mut child_flows = flows
        .iter()
        .filter(|flow| flow.group_id == parent_id)
        .collect::<Vec<_>>();
    child_flows.sort_by_key(|flow| flow.name.to_lowercase());

    entries.extend(child_flows.into_iter().map(|flow| TreeEntry {
        id: flow.id.clone(),
        name: yaml_file_name(&flow.name),
        depth,
        kind: TreeEntryKind::Flow,
        ancestor_group_ids: ancestor_group_ids.to_vec(),
    }));
}
