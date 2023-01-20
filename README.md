<!-- PROJECT SHIELDS -->

[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![GitHub Workflow Status][github-workflow-status]][github-workflow-status-url]
[![GitHub release (latest SemVer)][release-shield]][release-url]

# appswitcher-server

**appswitcher-server** renders a simple webpage which includes icon-based hyperlinks to common web applications used in your (corporate) environment.
The static page is designed to be included (e.g. by using an `iframe`) in your web applications to provide your users **a quick way to switch between these applications**.

appswitcher-server was **heavily inspired by** [Google's App Bar](https://support.google.com/accounts/answer/1714464?hl=en#zippy=%2Cswitch-between-apps), which provides a quick way to switch between Google Apps.

At [it@M](https://github.com/it-at-m) we use appswitcher-server internally to provide our customers a quick way to switch to internal or external web applications like the Social Intranet "WiLMA", the phone book and  many more web applications:

![Screenshot of an example of appswitcher-server's webpage](docs/with_default_tags.png)

The webpage of appswitcher-server is therefore embedded (mostly by using an `iframe`) in the App Bar of our web applications (= `client applications`):

![Screenshot of appswitcher-servers webpage embedded in a web application's app bar](docs/embedded_in_applications.png)

appswitcher-server can be easily configured **to include your own common web applications icon-hyperlinks**.

## Features

### Custom Applications

The included applications can be configured freely by adding them to the `apps` entry in [`application.yml`](src/main/resources/application.yml), for example:

```yml
appswitcher.apps:
  github:
    display-name: GitHub
    url: https://github.com/it-at-m
    image-path: https://avatars.githubusercontent.com/u/58515289?s=144&v=4
```

| Property | Description |
|----------|-------------|
| `display-name` | Name of the application |
| `url` | URL of the application, will be used for the hyperlink |
| `image-url` | URL for the applications image/icon. The image should be quadratic, size minimum: 48x48px, maximum 144x144px. The URL must be reachable by clients without any authentication. |


### Tags

Applications can be assigned (multiple) tags. For example, you could use tags to differ applications by their domain like `finance`, `development`, `customer-relations` and so on.

```yml
appswitcher.apps:
  github:
    display-name: GitHub
    url: https://github.com/it-at-m
    image-path: https://avatars.githubusercontent.com/u/58515289?s=144&v=4
    tags:
      - development
```

Client applications (= applications which embed appswitcher-servers webpage) can request specific tags by adding a corresponding query parameter `tags`. For example, if you embed the appswitcher in a developer-centric web application, you could request only apps which are tagged with `development`:

    https://appswitcher.mycompany.org?tags=development

You can also request multiple tags (e.g. `?tags=development,finance`). This way, every application gets included which has any of the requested tags.

If a client application requests no specific tags, **by default** only applications which are tagged as `global` are included.

### Keycloak integration

If your are using [Keycloak](https://www.keycloak.org/) with OpenID for access management and Single-Sign-On in your environment, you can additionally add (multiple) Client ID(s) to applications which use Keycloak/OpenID for access management.

```yml
appswitcher.apps:
  finance:
    display-name: Finance Reports
    url: https://finance.mycompany.com
    image-path: https://avatars.githubusercontent.com/u/58515289?s=144&v=4
    client-id:
      - finance
```

With this configuration, a icon-base hyperlink to the "Finance Reports" web application will only be included if the current users access token claim `audience` contains the client id (and [the tags are matching](#tags) the requested tags). This is based on the [Audience Support](https://www.keycloak.org/docs/latest/server_admin/#audience-support) in Keycloak.
Using the Keycloak integration you can "hide" web applications, to which the current user has no permission.

Keycloak integration is disabled by default. If enabled, appswitcher-server acts as a OpenID client application requiring a valid SSO session to retrieve an access token of the user.

This integration works best if your users operating system propagates a session to the browser and therefore Keycloak, for example Kerberos on Windows.

To make the Keycloak integration work properly, there are a couple of caveats (IFrame and Cookies, X-Frame-Options) to be aware of. See the Wiki :construction: for more details. 
 
## Configuration
 
appswitcher-server is a Spring Boot application and therefore it can be configured via [Spring environment abstraction](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).

| Environment variable  | System/Spring property  | Description | Default value |
| --------------------- | ----------------------- | ----------- | ------------- |
| SPRING_PROFILES_ACTIVE | `spring.profiles.active` | Comma seperated list of Spring profiles to activate (e.g. `keycloak`) | `` |

:construction: TODO

### Keycloak integration

Keycloak integration can be enabled by activating the Spring profile `keycloak` (see `SPRING_PROFILES_ACTIVE` in [Configuration](#configuration)).

| Environment variable  | System/Spring property  | Description | Default value |
| --------------------- | ----------------------- | ----------- | ------------- |

:construction: TODO

## Using

:construction: TODO

You can use the official Container image [itatm/appswitcher-server](https://hub.docker.com/r/itatm/appswitcher-server).

:construction: TODO

If you want to deploy appswitcher-server on a Kubernetes cluster, you can use the provided Helm chart. See [appswitcher-server-helm-chart][helm-chart-github] for more information and documentation.

## Build & Development

This project is built with technologies we use in our projects:

- Java
- Maven
- Spring Boot


```bash
mvn clean install

mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please open an issue with the tag "enhancement", fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Open an issue with the tag "enhancement"
2. Fork the Project
3. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
4. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
5. Push to the Branch (`git push origin feature/AmazingFeature`)
6. Open a Pull Request


## License

Distributed under the MIT License. See [LICENSE](LICENSE) file for more information.

## Contact

it@M - opensource@muenchen.de

[contributors-shield]: https://img.shields.io/github/contributors/it-at-m/appswitcher-server.svg?style=for-the-badge
[contributors-url]: https://github.com/it-at-m/appswitcher-server/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/it-at-m/appswitcher-server.svg?style=for-the-badge
[forks-url]: https://github.com/it-at-m/appswitcher-server/network/members
[stars-shield]: https://img.shields.io/github/stars/it-at-m/appswitcher-server.svg?style=for-the-badge
[stars-url]: https://github.com/it-at-m/appswitcher-server/stargazers
[issues-shield]: https://img.shields.io/github/issues/it-at-m/appswitcher-server.svg?style=for-the-badge
[issues-url]: https://github.com/it-at-m/appswitcher-server/issues
[license-shield]: https://img.shields.io/github/license/it-at-m/appswitcher-server.svg?style=for-the-badge
[license-url]: https://github.com/it-at-m/appswitcher-server/blob/main/LICENSE
[github-workflow-status]: https://img.shields.io/github/actions/workflow/status/it-at-m/appswitcher-server/build.yaml?style=for-the-badge
[github-workflow-status-url]: https://github.com/it-at-m/appswitcher-server/actions/workflows/build.yaml
[release-shield]: https://img.shields.io/github/v/release/it-at-m/appswitcher-server?sort=semver&style=for-the-badge
[release-url]: https://github.com/it-at-m/appswitcher-server/releases
[helm-chart-github]: https://github.com/it-at-m/appswitcher-server-helm-chart
