# Ashampoo Metadata Proxy Service

## Installation

Service to do metadata manipulation using HTTP methods.

The intended use is to allow metadata manipulation on big
files for self-hosted WebDAV.

This server needs fast access to the stored files.
So if you host a Nextcloud or Seafile docker container,
you would host this container in the same network.

## Local dev instructions

1. Launch a Nextcloud container using `docker run -d -p 8090:80 nextcloud`.
2. Open a web browser and navigate to http://localhost:8090/.
3. Log in using test as both the username and password.
4. Upload the `sample.jpg` file from the `resources` folder to the root directory.

## Contributions

Contributions are welcome! If you encounter any issues,
have suggestions for improvements, or would like to contribute new features,
please feel free to submit a pull request.

## Acknowledgements

* JetBrains for making [Kotlin](https://kotlinlang.org).

## License

This code is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
