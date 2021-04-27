 

## OAuth2

- 授权码 (authorization code) 模式：
  - 用户访问客户端，客户端将用户重定向到认证服务(携带)，如果用户确认授权，则会重定向到客户端指定的 url 并携带授权码
  - 客户端在重定向的请求中向认证服务器请求 token，需要携带 client_id, client_secret, code 以及重定向 url
  - 认证服务器校验了授权码以及重定向 url 后返回 access token 和 refresh token，客户端向服务器返回授权成功
- 简化 (implicit grant) 模式：简化模式不需要授权码，适合单页面
  - 客户端将用户重定向到认证服务器，如果用户授权则重定向到指
- 



## JWT

JWT (Json Web Token) 是 token 的一种形式，一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源。

jwt 是 session 以及 token 认证机制上发展而来，

