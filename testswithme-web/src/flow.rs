use crate::{
    api::client::get_flow,
    project::ProjectNavigation,
    session::{Session, clear_session},
};
use base64::{Engine, engine::general_purpose::STANDARD};
use leptos::{prelude::*, task::spawn_local};
use leptos_router::{
    NavigateOptions,
    components::A,
    hooks::{use_navigate, use_params_map},
};
use testswithme_api_rust::FlowItemDto;

#[component]
pub fn FlowPage(session: RwSignal<Option<Session>>) -> impl IntoView {
    let params = use_params_map();
    let flow_id = Memo::new(move |_| params.get().get("id").unwrap_or_default());
    let (flow, set_flow) = signal(None::<FlowItemDto>);
    let (content, set_content) = signal(String::new());
    let (error, set_error) = signal(None::<String>);
    let (is_loading, set_is_loading) = signal(true);
    let navigate = use_navigate();
    let active_session = session
        .get_untracked()
        .expect("FlowPage requires an authenticated session");

    let navigation_token = active_session.token.clone();
    let token = active_session.token;
    Effect::new(move |_| {
        let requested_flow_id = flow_id.get();
        set_flow.set(None);
        set_content.set(String::new());
        set_error.set(None);
        set_is_loading.set(true);

        let token = token.clone();
        spawn_local(async move {
            let result = get_flow(&token, &requested_flow_id).await;
            if flow_id.get_untracked() != requested_flow_id {
                return;
            }

            match result {
                Ok(response) => match decode_content(&response.flow.base64_content) {
                    Ok(decoded_content) => {
                        set_content.set(decoded_content);
                        set_flow.set(Some(response.flow));
                    }
                    Err(content_error) => set_error.set(Some(content_error)),
                },
                Err(flow_error) => set_error.set(Some(flow_error)),
            }
            set_is_loading.set(false);
        });
    });

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

            <section class="project-workspace">
                <ProjectNavigation
                    project_id=Signal::derive(move || {
                        flow.get().map(|flow| flow.project_id)
                    })
                    token=navigation_token
                    active_flow_id=Signal::derive(move || Some(flow_id.get()))
                />

                <section class="workspace-main">
                    <div class="flow-page-title">
                        <div>
                            <p class="eyebrow">"Flow file"</p>
                            <h1>
                                {move || {
                                    flow.get()
                                        .map(|flow| yaml_file_name(&flow.name))
                                        .unwrap_or_else(|| "Flow".to_owned())
                                }}
                            </h1>
                        </div>
                        <Show when=move || flow.get().is_some()>
                            <A
                                href=move || format!("/flow/{}/edit", flow_id.get())
                                attr:class="flow-edit-link"
                            >
                                "Edit"
                            </A>
                        </Show>
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
                                <div class="dashboard-loading" role="status">
                                    <span class="loader"></span>
                                    <span>"Loading flow file..."</span>
                                </div>
                            }
                        }
                    >
                        <Show when=move || error.get().is_none()>
                            <div class="flow-file">
                                <div class="flow-file-header">
                                    <span class="tree-icon" aria-hidden="true"></span>
                                    <span>
                                        {move || {
                                            flow.get()
                                                .map(|flow| yaml_file_name(&flow.name))
                                                .unwrap_or_default()
                                        }}
                                    </span>
                                </div>
                                <div class="flow-code">
                                    <pre class="flow-line-numbers" aria-hidden="true">
                                        {move || line_numbers(&content.get())}
                                    </pre>
                                    <pre class="flow-content"><code>{move || content.get()}</code></pre>
                                </div>
                            </div>
                        </Show>
                    </Show>
                </section>
            </section>
        </main>
    }
}

pub(crate) fn decode_content(base64_content: &str) -> Result<String, String> {
    let bytes = STANDARD
        .decode(base64_content)
        .map_err(|_| "The flow file contains invalid Base64 content.".to_owned())?;
    String::from_utf8(bytes).map_err(|_| "The flow file content is not valid UTF-8.".to_owned())
}

pub(crate) fn yaml_file_name(name: &str) -> String {
    if name.ends_with(".yaml") || name.ends_with(".yml") {
        name.to_owned()
    } else {
        format!("{name}.yaml")
    }
}

pub(crate) fn line_numbers(content: &str) -> String {
    (1..=content.split('\n').count())
        .map(|line_number| line_number.to_string())
        .collect::<Vec<_>>()
        .join("\n")
}
