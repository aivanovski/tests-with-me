use backend_api::LoginResponse;

const AUTH_TOKEN_KEY: &str = "testswithme.auth_token";
const USER_ID_KEY: &str = "testswithme.user_id";
const USER_NAME_KEY: &str = "testswithme.user_name";

#[derive(Clone)]
pub struct Session {
    pub token: String,
    pub user_name: String,
}

pub fn store_session(response: &LoginResponse) -> Result<(), String> {
    let storage = local_storage()
        .ok_or_else(|| "Login succeeded, but browser storage is unavailable.".to_owned())?;

    storage
        .set_item(AUTH_TOKEN_KEY, &response.token)
        .and_then(|_| storage.set_item(USER_ID_KEY, &response.user.id))
        .and_then(|_| storage.set_item(USER_NAME_KEY, &response.user.name))
        .map_err(|_| "Login succeeded, but the session could not be saved.".to_owned())
}

pub fn load_session() -> Option<Session> {
    let storage = local_storage()?;
    let token = storage.get_item(AUTH_TOKEN_KEY).ok().flatten()?;
    let user_id = storage.get_item(USER_ID_KEY).ok().flatten()?;
    let user_name = storage.get_item(USER_NAME_KEY).ok().flatten()?;

    if token.is_empty() || user_id.is_empty() || user_name.is_empty() {
        let _ = storage.remove_item(AUTH_TOKEN_KEY);
        let _ = storage.remove_item(USER_ID_KEY);
        let _ = storage.remove_item(USER_NAME_KEY);
        None
    } else {
        Some(Session { token, user_name })
    }
}

pub fn clear_session() -> Result<(), String> {
    let storage = local_storage().ok_or_else(|| "Browser storage is unavailable.".to_owned())?;

    storage
        .remove_item(AUTH_TOKEN_KEY)
        .and_then(|_| storage.remove_item(USER_ID_KEY))
        .and_then(|_| storage.remove_item(USER_NAME_KEY))
        .map_err(|_| "The session could not be cleared.".to_owned())
}

fn local_storage() -> Option<web_sys::Storage> {
    web_sys::window().and_then(|window| window.local_storage().ok().flatten())
}
