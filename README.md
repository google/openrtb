Google OpenRTB Libraries
----------------------------------------------------------------------

This library supports the OpenRTB specification, providing
bindings for all protobuf-supported languages, and additional
support for Java such as JSON serialization and validation.

See our [wiki](https://github.com/google/openrtb/wiki) to get started!
Use the Github issue tracker for bugs, RFEs for any support.


BUILDING NOTES
----------------------------------------------------------------------

You need: JDK 7, Maven 3.2, Protocol buffers (protoc) 2.6.1.
Building is supported from the command line with Maven and
from any IDE that can load Maven projects.

On Eclipse, the latest m2e is recommended but it can't run the code
generation step, so you need to run a "mvn install" from the command
line after checkout or after any mvn clean.


RELEASE NOTES
----------------------------------------------------------------------

## Version 0.8.4, 29-05-2015

* While you are distracted with Google IO, we're sneaking some painful
  but necessary breaking changes.  OpenRTB 2.3 finally provided clear,
  normative names for all objects, so we're finally adopting these names
  even though they have no impact at the JSON interop level:
  - `Impression` renamed to `Imp`
  - `PMP` renamed to `Pmp`
  - `Regulations` renamed to `Regs`
* Also renamed a small number of enum names and enumerated values, for
  different reasons (clarity, compatibility with internal Google systems):
  - `ApiFramework` renamed to `ApiFramework`
  - `CompanionType` renamed to `VASTCompanionType`
  - `AdType` renamed to `BannerAdType`
  - `Protocol` renamed to `VideoBidResponseProtocol`
  - `Linearity` renamed to `VideoLinearity`
  - `PlaybackMethod` renamed to `VideoPlaybackMethod`
  - `ContentDelivery` renamed to `ContentDeliveryMethod`
  - `Context` renamed to `ContentContext`
  - `NoBidReasonCode` renamed to `NoBidReason`
* Added to `DeviceType` some values we'd missed when they were introduced
  in OpenRTB 2.2: `PHONE`, `TABLET`, `CONNECTED_DEVICE`, `SET_TOP_BOX`.

## Version 0.8.3, 22-05-2015

* Added OpenRTB 2.1-compatible `CompanionAd`
* Expanded the range of IDs reserved for extensions
* Fixed some field IDs for compatibility with internal Google systems
* JSON parsing lenient with unknown fields
* Improve `OpenRtbJsonFactory/Reader/Writer` for subclassing.
  This can be used for uncompliant extensions, see `OpenRtbExtJsonTest`.

## Version 0.8.1, 29-04-2015

* All `*cat` fields in the model are now typed as [arrays of]
  the `ContentCategory` enum, instead of `string`s.
* `User.gender` is now typed as the `Gender` enum.
* `BidRequest/Deal.at` are now typed as the `AuctionType` enum.

## Version 0.8.0, 21-04-2015

* Lots of changes/improvements in the JSON extension support:
  - Public read/write methods to allow reuse by composition.
  - Extension registry is now per message type instead of "paths",
    static-typed, allowing reuse of messages as part of extensions.
  - Support for extensions of scalar types such as `int32`
  - Support for repeated extensions (of both message and scalar types).
  - New `OpenRtbJsonExtReader/Writer` make simpler to write extensions.
* OpenRTB model reviews:
  - Using protobuf deprecation for `Video.protocol`.
  - Documentation and ordering in sync with the latest specs.
  - Some `required/optional` updated to match the OpenRTB 2.3 spec.
  - `Content.sourcerelationship` changed type to `bool`.

## Version 0.8.0-beta4, 02-04-2015

* Test coverage reviews.

## Version 0.8.0-beta3, 31-03-2015

* Logging updates, mostly avoiding multiline logs (bad for syslog).

## Version 0.8.0-beta2, 13-03-2015

* Removed error-prone from build, new version had some issues too.

## Version 0.8.0-beta, 20-02-2015

* Support for Native Ads completed!
* Improvements in JSON support, esp. better compatibility with
  some OpenRTB-native exchanges and with OpenRTB 2.2
  (thanks to github.com/matzi11a & Sojern).
* `(App/Site/User/Content).keywords` are now arrays in the model.
  They still map to a single, CSV-format string in the JSON.
* The macro `${AUCTION_PRICE}` is not anymore translated to the
  bid price; this was a bug, this macro will proceed untouched
  (OpenRTB-native exchanges are supposed to expand it).
* Updated to latest error-prone; now Maven build works with JDK 8!

## Version 0.7.3, 02-12-2014

* Partial support for OpenRTB 2.3! The missing item is Native ads,
  which depends on the OpenRTB Native 1.0 spec (proposed final draft
  at this time). This support will come in a future update.
* Breaking change: The `Flags` enum was replaced by simple booleans.
* Breaking change: Renamed `DirectDeal` to just `Deal` (to better match
  the spec, although object names are not really standardized).
* New fields added (OpenRTB 2.3):
  - `BidRequest.test`
  - `Site.mobile`
  - `Device`: `w`, `h`, `pxratio`, `ppi`, `hwv`, `lmt`
  - `Bid`: `cat`, `bundle`
  - `Geo.utcoffset`
* Other fields added (missing from OpenRTB 2.2):
  - `Device`: `ifa`, `macsha1`, `macmd5`
  - `Impression`: `secure`
  - `Video`: `protocols` (the field `protocol` was deprecated; in the API
    it's renamed `deprecated_protocol`, JSON will be compatible)
  - `BidResponse`: `nbr`

## Version 0.7.2, 29-10-2014

* Updated to Protocol Buffers 2.6.1 (bugfix, doesn't require rebuilds).

## Version 0.7.1, 20-10-2014

* Updated to Protocol Buffers 2.6.0. Full rebuild recommended, the
  code generated by protoc 2.6.0 is NOT 100% compatible with 2.5.0.

## Version 0.7.0, 16-10-2014

* `OpenRtbUtils.filterBids()` now uses `Bid.Builder` instead of `Bid`.

## Version 0.6.6, 14-10-2014

* Javadocs for thead safety.
* Update Guava and Jackson libraries.

## Version 0.6.5, 18-08-2014

* `OpenRtbMapper` interface improved, supports all possible mappings.
* `ProtoUtils.filter()` optimized, benefits `OpenRtbUtils.filterBids()`.

## Version 0.6.4, 10-08-2014

* `OpenRtbJson` API review and javadocs.
* `OpenRtbValidator` reviews, improved logging.

## Version 0.6.3, 02-08-2014

* Fix floating-point fields to double precision.

## Version 0.6.2, 25-07-2014

* Remove dependencies jackson-databind, jackson-annotations.

## Version 0.6.1, 15-07-2014

* Build system improvements (Maven, Eclipse, NetBeans).

## Version 0.6, 10-07-2014

* Initial Open Source release.
