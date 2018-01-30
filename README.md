Google OpenRTB Libraries
----------------------------------------------------------------------

This library supports the OpenRTB specification, providing
bindings for all protobuf-supported languages, and additional
support for Java such as JSON serialization and validation.

See our [wiki](https://github.com/google/openrtb/wiki) to get started!
Use the Github issue tracker for bugs, RFEs or any support. Check the
[changelog](CHANGELOG.md) for detailed release notes.


BUILDING NOTES
----------------------------------------------------------------------

You need: JDK 8, Maven 3.2, Protocol buffers (protoc) 3.5.1.
Building is supported from the command line with Maven and
from any IDE that can load Maven projects.

On Eclipse, the latest m2e is recommended but it can't run the code
generation step, so you need to run a "mvn install" from the command
line after checkout or after any mvn clean.
