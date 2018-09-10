package io.swagger.v3.plugin.maven;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SwaggerResolveTest extends BetterAbstractMojoTestCase{

    protected static Logger LOGGER = LoggerFactory.getLogger(SwaggerResolveTest.class);

    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    public void testResolve() throws Exception {
        File pom = getTestFile("src/test/resources/pom.resolveToFile.xml");

        runTest(pom);
    }

    private void runTest(File pom) throws Exception {
        assertNotNull(pom);
        assertTrue( pom.exists() );

        SwaggerMojo swaggerMojo = (SwaggerMojo) lookupConfiguredMojo(pom, "resolve");
        assertNotNull( swaggerMojo );

        swaggerMojo.execute();
        final PlexusConfiguration config = extractPluginConfiguration("swagger-maven-plugin", pom);

        //String outputPath = config.getChild("outputPath").getValue();
        String outputPath = swaggerMojo.getOutputPath();
        String outputFile = config.getChild("outputFile").getValue();
        String format = config.getChild("outputFormat").getValue();
        Path path = Paths.get(outputPath, outputFile);
        File file = path.toFile();

        assertTrue(Files.isRegularFile(path));
        String content = FileUtils.readFileToString(file, "UTF-8");
        final OpenAPI openAPI;
        if (format.equals("YAML")) {
            openAPI = Yaml.mapper().readValue(content, OpenAPI.class);
        } else {
            openAPI = Json.mapper().readValue(content, OpenAPI.class);
        }

        assertNotNull(openAPI);
    }
}