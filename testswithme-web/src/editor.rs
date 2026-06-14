use crate::{
    api::client::{get_flow, update_flow},
    flow::{decode_content, yaml_file_name},
    session::{Session, clear_session},
};
use base64::{Engine, engine::general_purpose::STANDARD};
use leptos::{ev::SubmitEvent, prelude::*, task::spawn_local};
use leptos_router::{
    NavigateOptions,
    components::A,
    hooks::{use_navigate, use_params_map},
};
use testswithme_api_rust::FlowItemDto;

#[component]
pub fn EditorPage(session: RwSignal<Option<Session>>) -> impl IntoView {
    let params = use_params_map();
    let flow_id = params.get_untracked().get("id").unwrap_or_default();
    let flow_url = format!("/flow/{flow_id}");
    let (flow, set_flow) = signal(None::<FlowItemDto>);
    let (content, set_content) = signal(String::new());
    let (saved_content, set_saved_content) = signal(String::new());
    let (error, set_error) = signal(None::<String>);
    let (success, set_success) = signal(None::<String>);
    let (is_loading, set_is_loading) = signal(true);
    let (is_saving, set_is_saving) = signal(false);
    let navigate = use_navigate();
    let active_session = session
        .get_untracked()
        .expect("EditorPage requires an authenticated session");

    let token = active_session.token.clone();
    let requested_flow_id = flow_id.clone();
    spawn_local(async move {
        match get_flow(&token, &requested_flow_id).await {
            Ok(response) => match decode_content(&response.flow.base64_content) {
                Ok(decoded_content) => {
                    set_saved_content.set(decoded_content.clone());
                    set_content.set(decoded_content);
                    set_flow.set(Some(response.flow));
                }
                Err(content_error) => set_error.set(Some(content_error)),
            },
            Err(flow_error) => set_error.set(Some(flow_error)),
        }
        set_is_loading.set(false);
    });

    let token = active_session.token.clone();
    let flow_id_for_save = flow_id.clone();
    let save = move |event: SubmitEvent| {
        event.prevent_default();

        let updated_content = content.get();
        if updated_content.trim().is_empty() {
            set_success.set(None);
            set_error.set(Some("Flow content cannot be empty.".to_owned()));
            return;
        }
        if updated_content == saved_content.get() {
            return;
        }

        set_error.set(None);
        set_success.set(None);
        set_is_saving.set(true);

        let token = token.clone();
        let flow_id = flow_id_for_save.clone();
        spawn_local(async move {
            let encoded_content = STANDARD.encode(updated_content.as_bytes());
            match update_flow(&token, &flow_id, encoded_content).await {
                Ok(response) => {
                    set_flow.set(Some(response.flow));
                    set_saved_content.set(updated_content);
                    set_success.set(Some("Flow file saved.".to_owned()));
                }
                Err(update_error) => set_error.set(Some(update_error)),
            }
            set_is_saving.set(false);
        });
    };

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
                <A href=flow_url.clone() attr:class="back-link">
                    <span aria-hidden="true">"←"</span>
                    "Flow file"
                </A>

                <div class="flow-page-title">
                    <div>
                        <p class="eyebrow">"Edit flow file"</p>
                        <h1>
                            {move || {
                                flow.get()
                                    .map(|flow| yaml_file_name(&flow.name))
                                    .unwrap_or_else(|| "Flow".to_owned())
                            }}
                        </h1>
                    </div>
                </div>

                <Show when=move || error.get().is_some()>
                    <p class="form-error dashboard-error" role="alert">
                        {move || error.get().unwrap_or_default()}
                    </p>
                </Show>

                <Show when=move || success.get().is_some()>
                    <p class="form-success dashboard-success" role="status">
                        {move || success.get().unwrap_or_default()}
                    </p>
                </Show>

                <Show when=move || is_loading.get()>
                    <div class="dashboard-loading" role="status">
                        <span class="loader"></span>
                        <span>"Loading flow file..."</span>
                    </div>
                </Show>

                <form
                    class="editor-form"
                    class:editor-form-hidden=move || flow.get().is_none()
                    on:submit=save
                >
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
                        <textarea
                            class="flow-editor"
                            aria-label="Flow YAML content"
                            spellcheck="false"
                            prop:value=move || content.get()
                            on:input=move |event| {
                                set_content.set(event_target_value(&event));
                                set_error.set(None);
                                set_success.set(None);
                            }
                        ></textarea>
                    </div>

                    <div class="editor-actions">
                        <A href=flow_url.clone() attr:class="editor-cancel">
                            "Cancel"
                        </A>
                        <button
                            class="editor-save"
                            type="submit"
                            disabled=move || {
                                is_saving.get() || content.get() == saved_content.get()
                            }
                        >
                            <Show
                                when=move || !is_saving.get()
                                fallback=|| {
                                    view! {
                                        <span
                                            class="loader"
                                            aria-label="Saving flow file"
                                        ></span>
                                    }
                                }
                            >
                                "Save changes"
                            </Show>
                        </button>
                    </div>
                </form>
            </section>
        </main>
    }
}
