Google OpenRTB Libraries
----------------------------------------------------------------------

This library supports the OpenRTB specification, providing
bindings for all protobuf-supported languages, and additional
support for Java such as JSON serialization and validation.

See our [wiki](https://github.com/google/openrtb/wiki) to get started!
Use the Github issue tracker for bugs, RFEs for any support.


BUILDING NOTES
----------------------------------------------------------------------

You need: JDK 7, Maven 3.2, Protocol buffers (protoc) 2.5.0.
Building is supported from the command line with Maven and
from any IDE that can load Maven projects.

On Eclipse, the latest m2e is recommended but it can't run the code
generation step, so you need to run a "mvn install" from the command
line after checkout or after any mvn clean. Building with JDK 8 will
also not work, because we use the error-prone lint tool which is not
yet compatible with JDK 8 (once built, the library works with JDK 8).


RELEASE NOTES
----------------------------------------------------------------------

## Version 0.6.4, 10-08-2014

* OpenRtbJson API review and javadocs.
* OpenRtbValidator reviews, improved logging.

## Version 0.6.3, 02-08-2014

* Fix floating-point fields to double precision.

## Version 0.6.2, 25-07-2014

* Remove dependencies jackson-databind/jackson-annotations.

## Version 0.6.1, 15-07-2014

* Build system improvements (Maven, Eclipse, NetBeans).

## Version 0.6, 10-07-2014

* Initial Open Source release.
