package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.iabtechlab.openrtb.v2.OpenRtb;
import com.iabtechlab.openrtb.v2.OpenRtbExt;

public class OpenRtbRegsExtJsonReader extends OpenRtbJsonExtComplexReader<OpenRtb.BidRequest.Regs.Builder,
        OpenRtbExt.RegsExt.Builder> {

    public static final String GPC = "gpc";

    public OpenRtbRegsExtJsonReader() {
        super(OpenRtbExt.regs, false, GPC);
    }

    @Override
    protected void read(OpenRtbExt.RegsExt.Builder builder, JsonParser par) throws IOException {
        if (OpenRtbJsonUtils.getCurrentName(par).equals(GPC)) {
            builder.setGpc(par.nextTextValue());
        }
    }
}
