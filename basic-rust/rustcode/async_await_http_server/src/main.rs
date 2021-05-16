use std::convert::Infallible;
use std::net::SocketAddr;
use hyper::{Body, Request, Response, Server};
use hyper::service::{make_service_fn, service_fn};

async fn serve_req(_req: Request<Body>) -> Result<Response<Body>, Infallible> {
    Ok(Response::new(Body::from("hello, world!")))
}

#[tokio::main]
pub async fn main()-> Result<(), Box<dyn std::error::Error + Send + Sync>> {

    let make_svc = make_service_fn(|_conn| {
        async {Ok::<_, Infallible>(service_fn(serve_req))}
    });

    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    let server = Server::bind(&addr).serve(make_svc);

    println!("Listening on http://{}", addr);

    server.await?;

    Ok(())
}
