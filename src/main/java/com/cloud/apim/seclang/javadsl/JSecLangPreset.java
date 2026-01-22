package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.impl.compiler.Compiler;
import com.cloud.apim.seclang.impl.parser.AntlrParser;
import com.cloud.apim.seclang.model.CompiledProgram;
import com.cloud.apim.seclang.model.Configuration;
import com.cloud.apim.seclang.model.SecLangPreset;
import scala.collection.JavaConverters;

import java.util.HashMap;
import java.util.Map;

/**
 * A preset containing pre-compiled rules and associated data files.
 *
 * <p>Presets are used with {@link JSecLangEngineFactory} to compose rule sets
 * dynamically at runtime using the {@code @import_preset} directive.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * JSecLangPreset noFirefox = JSecLangPreset.withNoFiles(
 *     "no_firefox",
 *     "SecRule REQUEST_HEADERS:User-Agent \"@pm firefox\" \"id:1,phase:1,block\""
 * );
 *
 * Map<String, JSecLangPreset> presets = new HashMap<>();
 * presets.put("no_firefox", noFirefox);
 *
 * JSecLangEngineFactory factory = SecLang.factory(presets);
 * }</pre>
 */
public final class JSecLangPreset {

    private final SecLangPreset underlying;

    private JSecLangPreset(SecLangPreset underlying) {
        this.underlying = underlying;
    }

    /**
     * Create a preset with rules but no data files.
     *
     * @param name  the preset name
     * @param rules the SecLang rules as a string
     * @return a new preset
     */
    public static JSecLangPreset withNoFiles(String name, String rules) {
        return new JSecLangPreset(SecLangPreset.withNoFiles(name, rules, false, false));
    }

    /**
     * Create a preset with rules and data files.
     *
     * @param name  the preset name
     * @param rules the SecLang rules as a string
     * @param files map of virtual path to file content (for @pmFromFile, @ipMatchFromFile, etc.)
     * @return a new preset
     */
    public static JSecLangPreset withFiles(String name, String rules, Map<String, String> files) {
        scala.collection.immutable.Map<String, String> scalaFiles =
            JavaConverters.mapAsScalaMapConverter(files).asScala().toMap(
                scala.Predef.<scala.Tuple2<String, String>>conforms()
            );
        return new JSecLangPreset(SecLangPreset.withFiles(name, rules, scalaFiles, false, false));
    }

    /**
     * Get the preset name.
     */
    public String getName() {
        return underlying.name();
    }

    /**
     * Get the data files associated with this preset.
     */
    public Map<String, String> getFiles() {
        return new HashMap<>(JavaConverters.mapAsJavaMapConverter(underlying.files()).asJava());
    }

    /**
     * Get the underlying Scala SecLangPreset.
     */
    SecLangPreset toScala() {
        return underlying;
    }

    /**
     * Create from an existing Scala SecLangPreset.
     */
    public static JSecLangPreset fromScala(SecLangPreset preset) {
        return new JSecLangPreset(preset);
    }
}
