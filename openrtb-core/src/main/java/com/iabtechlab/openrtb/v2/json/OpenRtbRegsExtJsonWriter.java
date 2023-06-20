package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt.RegsExt;

public class OpenRtbRegsExtJsonWriter extends OpenRtbJsonExtWriter<RegsExt> {

    @Override
    protected void write(RegsExt regsExt, JsonGenerator gen) throws IOException {
        if (regsExt.hasGpc()) {
            gen.writeStringField("gpc", regsExt.getGpc());
        }
    }
}
