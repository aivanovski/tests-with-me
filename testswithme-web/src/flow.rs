use crate::{
    api::client::get_flow,
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
    let flow_id = params.get_untracked().get("id").unwrap_or_default();
    let (flow, set_flow) = signal(None::<FlowItemDto>);
    let (content, set_content) = signal(String::new());
    let (error, set_error) = signal(None::<String>);
    let (is_loading, set_is_loading) = signal(true);
    let navigate = use_navigate();
    let active_session = session
        .get_untracked()
        .expect("FlowPage requires an authenticated session");

    let token = active_session.token;
    let requested_flow_id = flow_id.clone();
    spawn_local(async move {
        match get_flow(&token, &requested_flow_id).await {
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
        <main class="dashboard-page">
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

            <section class="dashboard-content project-content">
                <A
                    href=move || {
                        flow.get()
                            .map(|flow| format!("/project/{}", flow.project_id))
                            .unwrap_or_else(|| "/dashboard".to_owned())
                    }
                    attr:class="back-link"
                >
                    <span aria-hidden="true">"←"</span>
                    "Project flows"
                </A>

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
                            href=format!("/flow/{flow_id}/edit")
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
                            <pre class="flow-content"><code>{move || content.get()}</code></pre>
                        </div>
                    </Show>
                </Show>
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
