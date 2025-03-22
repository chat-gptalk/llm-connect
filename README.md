# LLM-connect

**LLM-connect** is an LLM (Large Language Model) gateway built on Spring AI. It provides a unified API entry point for accessing multiple LLM services, offering features like usage control, access restrictions, logging, and monitoring. It aims to streamline the integration and management of LLM APIs while ensuring security and high availability.

---

## Features

- **Built on Spring AI**: Efficient and extensible LLM gateway leveraging the Spring AI framework for rapid integration and service management.
- **Unified LLM Gateway**: One API entry point to access multiple LLM services, simplifying integration.
- **Smart Usage Control**: Flexible management of usage limits, including API Key, project-based restrictions, call frequency, and quota management.
- **API Key & Project-Level Access Control**: Fine-grained control over who can access what resources, including configurable call limits, model restrictions, and usage quotas.
- **Dynamic Logging and Monitoring**: Real-time logging of API requests with powerful filtering and querying capabilities to track performance metrics and detect issues.
- **Security & Protection**: Multiple layers of security including identity authentication, access control, IP whitelisting, and encrypted API requests to ensure data integrity and prevent unauthorized usage.
- **High Availability & Scalability**: Designed for high availability, load balancing, and horizontal scalability to handle varying levels of traffic.
- **Flexible Model Configuration**: Easily configurable LLM models with customizable request and response formats.
- **Real-Time Data Analytics & Reporting**: Comprehensive analytics and reports for usage trends, call statistics, and error rates to optimize API usage.

---

## Installation

To get started with **LLM-connect**, follow the steps below:

### Prerequisites

- Java 17 or higher
- Maven or Gradle for build management
- Spring Boot dependencies

### Clone the repository

```bash
git clone https://github.com/chat-gptalk/llm-connect.git
cd llm-connect
```


## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing
We welcome contributions to LLM-connect! To contribute, please fork the repository, create a new branch, and submit a pull request. Make sure your code adheres to the following guidelines:

- Follow the Spring Boot conventions.
- Write clear, concise commit messages.
- Ensure all tests pass before submitting your pull request.