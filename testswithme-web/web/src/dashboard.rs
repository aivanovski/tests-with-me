use crate::{
    api::client::get_projects,
    session::{Session, clear_session},
};
use backend_api::ProjectsItemDto;
use leptos::{prelude::*, task::spawn_local};
use leptos_router::{NavigateOptions, components::A, hooks::use_navigate};

#[component]
pub fn DashboardPage(session: RwSignal<Option<Session>>) -> impl IntoView {
    let (projects, set_projects) = signal(Vec::<ProjectsItemDto>::new());
    let (error, set_error) = signal(None::<String>);
    let (is_loading, set_is_loading) = signal(true);
    let navigate = use_navigate();
    let active_session = session
        .get_untracked()
        .expect("DashboardPage requires an authenticated session");

    let token = active_session.token;
    spawn_local(async move {
        match get_projects(&token).await {
            Ok(response) => set_projects.set(response.projects),
            Err(projects_error) => set_error.set(Some(projects_error)),
        }

        set_is_loading.set(false);
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
        Err(logout_error) => {
            set_error.set(Some(logout_error));
        }
    };

    view! {
        <main class="dashboard-page">
            <header class="dashboard-header">
                <div class="dashboard-brand">
                    <span class="brand-mark" aria-hidden="true">
                        <span></span>
                        <span></span>
                        <span></span>
                    </span>
                    <span>"TestsWithMe"</span>
                </div>

                <div class="dashboard-account">
                    <span>{active_session.user_name}</span>
                    <button class="dashboard-logout" type="button" on:click=logout>
                        "Log out"
                    </button>
                </div>
            </header>

            <section class="dashboard-content">
                <div class="dashboard-title">
                    <div>
                        <p class="eyebrow">"Workspace"</p>
                        <h1>"Projects"</h1>
                    </div>
                    <p>{move || format!("{} total", projects.get().len())}</p>
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
                                <span>"Loading projects..."</span>
                            </div>
                        }
                    }
                >
                    <Show
                        when=move || !projects.get().is_empty()
                        fallback=|| {
                            view! {
                                <div class="empty-projects">
                                    <h2>"No projects yet"</h2>
                                    <p>"Projects linked to your account will appear here."</p>
                                </div>
                            }
                        }
                    >
                        <div class="project-grid">
                            <For
                                each=move || projects.get()
                                key=|project| project.id.clone()
                                children=|project| view! { <ProjectCard project /> }
                            />
                        </div>
                    </Show>
                </Show>
            </section>
        </main>
    }
}

#[component]
fn ProjectCard(project: ProjectsItemDto) -> impl IntoView {
    let project_url = format!("/project/{}", project.id);
    let initial = project
        .name
        .chars()
        .next()
        .unwrap_or('?')
        .to_uppercase()
        .to_string();
    let image_alt = format!("{} icon", project.name);
    let image_url = project
        .image_url
        .filter(|image_url| !image_url.trim().is_empty());
    let (show_image, set_show_image) = signal(image_url.is_some());
    let description = project
        .description
        .filter(|description| !description.trim().is_empty())
        .unwrap_or_else(|| "No description provided.".to_owned());

    view! {
        <A href=project_url attr:class="project-card">
            <article>
                <div class="project-card-heading">
                    <div class="project-icon">
                        <span>{initial}</span>
                        {image_url.map(|image_url| {
                            view! {
                                <img
                                    class="project-image"
                                    class:project-image-hidden=move || !show_image.get()
                                    src=image_url
                                    alt=image_alt
                                    on:error=move |_| set_show_image.set(false)
                                />
                            }
                        })}
                    </div>
                    <div>
                        <h2>{project.name}</h2>
                        <p>{project.package_name}</p>
                    </div>
                </div>
                <p class="project-description">{description}</p>
            </article>
        </A>
    }
}
