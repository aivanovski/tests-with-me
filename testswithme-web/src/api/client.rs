use testswithme_api_rust::{
    FlowResponse, FlowsResponse, GetProjectsResponse, GroupsResponse, LoginRequest, LoginResponse,
};
use gloo_net::http::{Request, Response};
use serde::Deserialize;

const API_URL: &str = "http://127.0.0.1:8080";

#[derive(Deserialize)]
pub struct ErrorResponse {
    message: String,
}

pub async fn login(username: String, password: String) -> Result<LoginResponse, String> {
    let request = LoginRequest { username, password };
    let url = format!("{}/login", API_URL);
    let response = Request::post(&url)
        .header("Content-Type", "application/json")
        .json(&request)
        .map_err(|error| format!("Unable to prepare login request: {error}"))?
        .send()
        .await
        .map_err(|_| "Unable to reach the TestsWithMe server.".to_owned())?;

    parse_response(response, "login").await
}

pub async fn get_projects(token: &str) -> Result<GetProjectsResponse, String> {
    get(token, "project", "projects").await
}

pub async fn get_groups(token: &str) -> Result<GroupsResponse, String> {
    get(token, "group", "groups").await
}

pub async fn get_flows(token: &str) -> Result<FlowsResponse, String> {
    get(token, "flow", "flows").await
}

pub async fn get_flow(token: &str, flow_id: &str) -> Result<FlowResponse, String> {
    get(token, &format!("flow/{flow_id}"), "flow").await
}

async fn get<T>(token: &str, path: &str, request_name: &str) -> Result<T, String>
where
    T: for<'de> Deserialize<'de>,
{
    let url = format!("{}/{}", API_URL, path);
    let response = Request::get(&url)
        .header("Authorization", &format!("Bearer {token}"))
        .send()
        .await
        .map_err(|_| "Unable to reach the TestsWithMe server.".to_owned())?;

    parse_response(response, request_name).await
}

async fn parse_response<T>(response: Response, request_name: &str) -> Result<T, String>
where
    T: for<'de> Deserialize<'de>,
{
    if response.ok() {
        response
            .json::<T>()
            .await
            .map_err(|_| format!("The server returned an invalid {request_name} response."))
    } else {
        let status = response.status();
        let message = response
            .json::<ErrorResponse>()
            .await
            .map(|error| error.message)
            .unwrap_or_else(|_| format!("Request failed with status {status}."));

        Err(message)
    }
}
