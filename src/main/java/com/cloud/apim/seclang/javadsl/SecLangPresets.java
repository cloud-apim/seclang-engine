package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.scaladsl.SecLangPresets$;

public class SecLangPresets {
    public JSecLangPreset coreruleset() {
        return JSecLangPreset.fromScala(SecLangPresets$.MODULE$.coreruleset());
    }
    public JSecLangPreset corerulesetLocalFs(String path) {
        return JSecLangPreset.fromScala(SecLangPresets$.MODULE$.corerulesetLocalFs(path));
    }
}
