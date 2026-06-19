use testswithme_web::dashboard::DashboardPage;
use testswithme_web::editor::EditorPage;
use testswithme_web::flow::FlowPage;
use testswithme_web::login::LoginPage;
use testswithme_web::project::ProjectPage;
use testswithme_web::session::{Session, load_session};

use leptos::prelude::*;
use leptos_router::{components::*, path};

fn main() {
    mount_to_body(App);
}

#[component]
fn App() -> impl IntoView {
    let session = RwSignal::new(load_session());

    view! {
        <Router>
            <Routes fallback=|| view! { <Redirect path="/" /> }>
                <Route
                    path=path!("/")
                    view=move || view! { <HomeRoute session /> }
                />
                <Route
                    path=path!("/login")
                    view=move || view! { <LoginRoute session /> }
                />
                <Route
                    path=path!("/dashboard")
                    view=move || view! { <DashboardRoute session /> }
                />
                <Route
                    path=path!("/project/:id")
                    view=move || view! { <ProjectRoute session /> }
                />
                <Route
                    path=path!("/flow/:id")
                    view=move || view! { <FlowRoute session /> }
                />
                <Route
                    path=path!("/flow/:id/edit")
                    view=move || view! { <EditorRoute session /> }
                />
            </Routes>
        </Router>
    }
}

#[component]
fn HomeRoute(session: RwSignal<Option<Session>>) -> impl IntoView {
    if session.get_untracked().is_some() {
        view! { <Redirect path="/dashboard" /> }.into_any()
    } else {
        view! { <Redirect path="/login" /> }.into_any()
    }
}

#[component]
fn LoginRoute(session: RwSignal<Option<Session>>) -> impl IntoView {
    if session.get_untracked().is_some() {
        view! { <Redirect path="/dashboard" /> }.into_any()
    } else {
        view! { <LoginPage session /> }.into_any()
    }
}

#[component]
fn DashboardRoute(session: RwSignal<Option<Session>>) -> impl IntoView {
    if session.get_untracked().is_some() {
        view! { <DashboardPage session /> }.into_any()
    } else {
        view! { <Redirect path="/login" /> }.into_any()
    }
}

#[component]
fn ProjectRoute(session: RwSignal<Option<Session>>) -> impl IntoView {
    if session.get_untracked().is_some() {
        view! { <ProjectPage session /> }.into_any()
    } else {
        view! { <Redirect path="/login" /> }.into_any()
    }
}

#[component]
fn FlowRoute(session: RwSignal<Option<Session>>) -> impl IntoView {
    if session.get_untracked().is_some() {
        view! { <FlowPage session /> }.into_any()
    } else {
        view! { <Redirect path="/login" /> }.into_any()
    }
}

#[component]
fn EditorRoute(session: RwSignal<Option<Session>>) -> impl IntoView {
    if session.get_untracked().is_some() {
        view! { <EditorPage session /> }.into_any()
    } else {
        view! { <Redirect path="/login" /> }.into_any()
    }
}
