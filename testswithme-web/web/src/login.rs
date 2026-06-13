use crate::{
    api::client::login,
    session::{Session, store_session},
};
use leptos::{ev::SubmitEvent, prelude::*, task::spawn_local};
use leptos_router::{NavigateOptions, hooks::use_navigate};

#[component]
pub fn LoginPage(session: RwSignal<Option<Session>>) -> impl IntoView {
    let (username, set_username) = signal(String::new());
    let (password, set_password) = signal(String::new());
    let (error, set_error) = signal(None::<String>);
    let (is_loading, set_is_loading) = signal(false);
    let navigate = use_navigate();

    let submit = move |event: SubmitEvent| {
        event.prevent_default();

        let username = username.get().trim().to_owned();
        let password = password.get();

        if username.is_empty() {
            set_error.set(Some("Enter your username.".to_owned()));
            return;
        }

        if password.is_empty() {
            set_error.set(Some("Enter your password.".to_owned()));
            return;
        }

        set_error.set(None);
        set_is_loading.set(true);

        let navigate = navigate.clone();
        spawn_local(async move {
            match login(username, password).await {
                Ok(response) => {
                    if let Err(storage_error) = store_session(&response) {
                        set_error.set(Some(storage_error));
                    } else {
                        session.set(Some(Session {
                            token: response.token,
                            user_name: response.user.name,
                        }));
                        navigate(
                            "/dashboard",
                            NavigateOptions {
                                replace: true,
                                ..Default::default()
                            },
                        );
                    }
                }
                Err(login_error) => set_error.set(Some(login_error)),
            }

            set_is_loading.set(false);
        });
    };

    view! {
        <main class="login-page">
            <section class="brand-panel" aria-label="TestsWithMe overview">
                <div class="brand-content">
                    <div class="brand-message">
                        <p class="eyebrow">"Test automation workspace"</p>
                        <h1>"Ship reliable tests with TestsWithMe."</h1>
                    </div>
                </div>
            </section>

            <section class="form-panel">
                <div class="form-wrapper">
                    <div class="mobile-brand">
                        <span class="brand-mark" aria-hidden="true">
                            <span></span>
                            <span></span>
                            <span></span>
                        </span>
                        <span>"TestsWithMe"</span>
                    </div>

                    <header>
                        <p class="eyebrow">"Welcome back"</p>
                        <h2>"Log in to your account"</h2>
                        <p class="form-intro">
                            "Enter your credentials to continue to TestsWithMe."
                        </p>
                    </header>

                    <form on:submit=submit novalidate>
                        <label for="username">"Username"</label>
                        <input
                            id="username"
                            name="username"
                            type="text"
                            autocomplete="username"
                            autofocus
                            placeholder="Enter your username"
                            prop:value=move || username.get()
                            on:input=move |event| {
                                set_username.set(event_target_value(&event));
                                set_error.set(None);
                            }
                        />

                        <div class="password-label">
                            <label for="password">"Password"</label>
                        </div>
                        <input
                            id="password"
                            name="password"
                            type="password"
                            autocomplete="current-password"
                            placeholder="Enter your password"
                            prop:value=move || password.get()
                            on:input=move |event| {
                                set_password.set(event_target_value(&event));
                                set_error.set(None);
                            }
                        />

                        <Show when=move || error.get().is_some()>
                            <p class="form-error" role="alert">
                                {move || error.get().unwrap_or_default()}
                            </p>
                        </Show>

                        <button type="submit" disabled=move || is_loading.get()>
                            <Show
                                when=move || !is_loading.get()
                                fallback=|| view! { <span class="loader" aria-label="Logging in"></span> }
                            >
                                "Log in"
                            </Show>
                        </button>
                    </form>

                    <p class="join-message">
                        "Don't have an account? "
                        <a href="#" on:click=move |event| event.prevent_default()>"Join TestsWithMe"</a>
                    </p>
                </div>
            </section>
        </main>
    }
}
