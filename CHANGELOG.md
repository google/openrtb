RELEASE NOTES
----------------------------------------------------------------------

## Version 1.4.5, 07-08-2017
- Only dependency updates.

## Version 1.4.3, 27-06-2017
- Fixed very minor bug in JSON support.

## Version 1.4.1, 23-05-2017
- Now using Protobuf 3.3.1.

## Version 1.4.0, 18-04-2017
* Support for OpenRTB 2.5!

## Version 1.3.0, 03-02-2017
* Updated Protocol Buffers to v3.2.0!  You will need to install this
  exact version of protobuf, have it in the PATH, rebuild everything.

## Version 1.2.2, 09-01-2017
* OpenRTB proto update (docs only).

## Version 1.2.1, 09-11-2016
* Added some utility methods for JSON extensions.

## Version 1.2.0, 28-10-2016
* Removed `ObjectMapper` interface.

## Version 1.1.0, 14-09-2016
* Support for OpenRTB 2.4 & OpenRTB Native 1.1.
  - This support needed source-breaking changes; several enums are now
    used by new messages so their original scope was a problem, so now
    ALL enums moved to the top-level scope. Existing source code will
    only need changes in imports.
  - Because of the previous change, a few enum names and enum value
    names had to be renamed to avoid conflicts (protobuf requires unique
    enum values for all enums of the same scope): `DeviceType.PHONE` ->
    `HIGHEND_PHONE`; `AdUnitId.CUSTOM` -> `ADUNITID_CUSTOM`.
  - The `Gender` enum was moved out of the proto, since the spec doesn't
    use numeric values for this and the single-letter codes like `"M"`
    are too terse for enumerated value names.  A new, regular Java enum
    `Gender` was added as replacement, providing a better API.

## Version 1.0.5, 13-06-2016
* Support for Native objects embedded as direct nodes inside the core
  model, i.e. `"native": { "request": { "ver": "1", ... } }`, similar
  for response's `adm` field.  This is an extension of the OpenRTB spec,
  but it's now clearly allowed by the standard and increasingly popular.
  Some exchanges may use alternate names for the `request` and `adm`
  fields, and this core lib can't have this kind of exchange-specific
  behavior, but now it should be easy to support on top of the library.
  Thanks @bundeskanzler4711 for this contribution!

## Version 1.0.4, 16-05-2016
* Fixed native response's `Video.vasttag` to scalar value.
* Several documentation updates in the openrtb protobuf.
* Reviews in `OpenRtbJsonUtils`.
* `SnippetProcessor` allows reusing the internal string buffer;
  supports processing extended fields again but now as an option.
* Reintroduced JDK 7 support in the "compat" branch.

## Version 1.0.3, 07-03-2016
* `SnippetProcessor` allows subclasses to extend the list of fields
  (including `Bid` extensions) that support macros, and drops the
  built-in support to `adm` only which is more OpenRTB-compliant.

## Version 1.0.2, 26-02-2016
* Fix `ProtoUtils.filter()` for recursive filtering.
* JSON writer doesn't emit the top-level `native` field for native
  request or response objects. That was a poorly specified part of
  OpenRTB 2.3 & Native 1.0, clarified in the next releases but even
  in 2.4/1.0 this extra field should not be used.  The factory has a
  new option `rootNativeField` to opt in the legacy writer's behavior.
  (The reader always accepted both forms.)
* Mapping documentation added to the proto!  The Google Sheet used for
  this documentation is now deprecated; just look up the proto, or the
  API docs in protoc-generated model classes (e.g. Javadoc for Java).

## Version 1.0.1, 04-02-2016
* Minor reviews in `OpenRtbJsonUtils`, javadocs, tests.

## Version 1.0.0, 04-01-2016
* Happy new year!!  And here's the one-dot-zero release of the library.
  Series 1.0.x is now frozen, may be updated only for bugfixes.
* Only cleanups in this release: All deprecated methods removed;
  Dependency updates; Minor javadoc and test reviews.

## Version 0.9.8, 02-11-2015
* Improvements to `OpenRtbUtils`.

## Version 0.9.7, 29-10-2015
* THE BIG MOVE TO JAVA 8! The library now requires JDK 8, and takes
  advantage of new APIs/features of Java 8.
  - `ProtoUtils` and `OpenRtbUtils`: parameters with `Function` or
    `Predicate` APIs from Guava were changed to use `java.util.function`.
    Most code using these utilities should compile after fixing imports;
    code already passing lambdas for those parameters must be recompiled
    but needs no changes. Some utilities have new variants using streams,
    for example `OpenRtbUtils.bidStreamWith()`; if you like streams, the
    new methods are easier and often more efficient than calling the old
    methods and doing your own conversion to a `Stream`.
  - We won't maintain a JDK 7 compatible version of the library; notice
    that JDK 7 was EOL'd since April 2015.

## Version 0.9.6, 26-10-2015
* JSON factory now enables strict parsing by default; previous behavior
  was a bug. Use `setStrict(false)` if this breaks things for you.
* In non-strict mode, the JSON readers will now return `null` instead
  of an exception if the input is empty.

## Version 0.9.5, 02-10-2015
* No changes! This release contains only a round of reviews in code
  style, javadocs, and unit tests (converted to Truth).

## Version 0.9.4, 24-09-2015
* `OpenRtbUtils` improved for filtering impressions with Native ads.

## Version 0.9.3, 22-09-2015
* OpenRTB proto schema review:
  - Documentation updates; some fixes to field defaults.
  - Mutually exclusive: `BidRequest.site/app`; `Bid.adm/adm_native`;
    `Native.request/request_native` (introducing the latter);
    `Asset.title/img/video/data` for both Native request and response.
* JSON support:
  - New config `OpenRtbJsonFactory.strict`.
  - Parsing of unknown enums improved, lenient and strict options.
  - Reviews in the JSON extension APIs, `OpenRtbJsonExtWriter` and
    `OpenRtbJsonExtReader`. The latter has breaking changes, and some
    implementations need to override `OpenRtbJsonExtComplexReader`.

## Version 0.9.2, 01-09-2015
* Fixed typo `NoBidReason.BLOCKED_PUBISHER` -> `BLOCKED_PUBLISHER`.

## Version 0.9.1, 15-07-2015
* The `SnippetProcessor` API was improved for extensibility:
  - Subclasses can use specializations of `SnippetProcessorContext`.
  - Recursive macro expansion is supported.  Notice the processor will
    detect simple infinite recursion (`${X}` => `${X}` will stop: good
    for "pass-through" macros), but not indirect recursion (`${X}` =>
    `${Y}` => `${X}` or `${X}` => `x${X}`: `StackOverflowError`).

## Version 0.9.0, 01-07-2015
* This release brings some breaking changes, but hopefully the last;
  now pending only field testing before the first stable, v1.0 release.
* Merge Native Ads into the same proto descriptor, so the generated
  model moves from `OpenRtbNative.*` to `OpenRtb.*`. The Native-specific
  and incomplete `Video` object is gone, now native requests reuse the
  core model's `Video`.  You need only to fix `import` statements.
* Reverted some recent changes that would cause interoperability issues:
  - Revert all `keywords` fields to be single strings with internal CSV
    content (instead of string arrays with automatic CSV conversion).
  - Revert all `ContentCategory` fields (`bcat`, `cat`, `sectioncat`,
    `pagecat`), and `User.gender`, from enum to string. You can still
    use the enum, converting with utility methods from `OpenRtbUtils`.
    The JSON serializer will also validate the string fields.

## Version 0.8.6, 24-06-2015

* Support for the OpenRTB 2.3.1 specification:
  - Macro `${AUCTION_BID_ID}` expands to `BidResponse.bidid` (not `.id`)
  - `User.buyerid` fixed back to `buyeruid`. This was correct already in
    the library, but now the JSON parser will accept the wrong field name
    `buyerid` for backwards compatibility with the broken 2.3.0 spec.
* Fixed JSON serialization of Native Ads.

## Version 0.8.5, 11-06-2015

* Fix `Bid.bcat` cardinality to repeated.
* Fix `Asset.req` name to `required`.
* Added `[packed=true]` qualifier to several fields.

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
