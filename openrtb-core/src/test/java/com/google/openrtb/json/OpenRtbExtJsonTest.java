/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.openrtb.json;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.SetMultimap;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.TestExt;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;
import org.junit.Test;

/**
 * Tests for {@link OpenRtbJsonFactory}, {@link OpenRtbJsonReader}, {@link OpenRtbJsonWriter}.
 * Specifically, subclassing all the above.
 *
 * <p>This test shows how to extend the JSON support to process non-standard extensions,
 * i.e. new fields added directly to the model objects (not to "ext.*" subfields).
 * Using the real-world example of MoPub's BidRequest.crtype field.
 * Here, we map the non-standard extensions to a regular extension node in the model,
 * showing how you can keep OpenRTB compliance in the model even for some exchange
 * that adds non-compliant extensions at the JSON level.
 * We're using the BidRequest.testRequest1.test1 extension to map the uncompliant-in-JSON
 * extension, in this case you will NOT install any OpenRtbJsonExt* handlers for that.
 */
public class OpenRtbExtJsonTest {

  static class MyOpenRtbJsonFactory extends OpenRtbJsonFactory {
    protected MyOpenRtbJsonFactory(
        @Nullable JsonFactory jsonFactory,
        boolean strict,
        boolean rootNativeField,
        boolean forceNativeAsObject,
        @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
        @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
      super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    protected MyOpenRtbJsonFactory(MyOpenRtbJsonFactory config) {
      super(config);
    }

    public static MyOpenRtbJsonFactory create() {
      return new MyOpenRtbJsonFactory(null, false, true, false, null, null);
    }

    @Override public OpenRtbJsonReader newReader() {
      return new MyOpenRtbJsonReader(new MyOpenRtbJsonFactory(this));
    }

    @Override public OpenRtbJsonWriter newWriter() {
      return new MyOpenRtbJsonWriter(new MyOpenRtbJsonFactory(this));
    }
  }

  static class MyOpenRtbJsonReader extends OpenRtbJsonReader {
    public MyOpenRtbJsonReader(MyOpenRtbJsonFactory factory) {
      super(factory);
    }

    @Override protected void readBidRequestField(
        JsonParser par, BidRequest.Builder req, String fieldName)
        throws IOException {
      switch (fieldName) {
        case "crtype": {
          req.setExtension(TestExt.crtype, par.getText());
          break;
        }

        default:
          super.readBidRequestField(par, req, fieldName);
      }
    }
  }

  static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {
    public MyOpenRtbJsonWriter(MyOpenRtbJsonFactory factory) {
      super(factory);
    }

    @Override protected void writeBidRequestFields(
        BidRequest req, JsonGenerator gen) throws IOException {
      super.writeBidRequestFields(req, gen);

      if (req.hasExtension(TestExt.crtype)) {
        gen.writeStringField("crtype", req.getExtension(TestExt.crtype));
      }
    }
  }

  @Test
  public void testRequest_uncompliantExtension() throws IOException {
    BidRequest req1 = BidRequest.newBuilder()
        .setId("0")
        .setExtension(TestExt.crtype, "5")
        .build();
    MyOpenRtbJsonFactory factory = MyOpenRtbJsonFactory.create();
    String jsonReq = factory.newWriter().writeBidRequest(req1);
    assertThat(jsonReq).isEqualTo("{\"id\":\"0\",\"crtype\":\"5\"}");
    assertThat(factory.newReader().readBidRequest(jsonReq)).isEqualTo(req1);
  }
}
