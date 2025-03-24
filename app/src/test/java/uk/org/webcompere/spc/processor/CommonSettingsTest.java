package uk.org.webcompere.spc.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.spc.model.PropertiesFileFactory.createFile;

class CommonSettingsTest {

    private SpcArgs multipleArgs = new SpcArgs();
    private SpcArgs fullArgs = new SpcArgs();
    private SpcArgs noneArgs = new SpcArgs();
    private SpcArgs consistentArgs = new SpcArgs();

    @BeforeEach
    void beforeEach() {
        multipleArgs.setCommonProperties(SpcArgs.CommonPropertiesMode.multiple);
        fullArgs.setCommonProperties(SpcArgs.CommonPropertiesMode.full);
        consistentArgs.setCommonProperties(SpcArgs.CommonPropertiesMode.consistent);
    }

    @Test
    void whenThereIsACommonFileAndAPropertiesFileThenNoFilesAreCreated() {
        PropertiesFile dev = createFile("application-dev.properties", Map.of());
        PropertiesFile common = createFile("application.properties", Map.of());

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, common), fullArgs);
        assertThat(result).hasSize(2);
    }

    @Test
    void whenThereIsNotACommonFileThenItIsCreated() {
        PropertiesFile dev = createFile("application-dev.properties", Map.of());
        PropertiesFile prod = createFile("application-prod.properties", Map.of());

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod), fullArgs);
        assertThat(result).hasSize(3);
    }

    @Test
    void whenThereIsNotACommonFileButOnlyOneOtherThenNothingIsCreated() {
        PropertiesFile dev = createFile("application-dev.properties", Map.of());

        List<PropertiesFile> result = CommonSettings.process(List.of(dev), fullArgs);
        assertThat(result).hasSize(1);
    }

    @Test
    void whenThereIsNotACommonFileButInModeNoneThenNoCommonIsCreated() {
        PropertiesFile dev = createFile("application-dev.properties", Map.of());
        PropertiesFile prod = createFile("application-prod.properties", Map.of());

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod), noneArgs);
        assertThat(result).hasSize(2);
    }

    @Test
    void whenACommonPropertyExistsInBothFilesThenItIsPutInCommon() {
        String commonProperty = "server.port";
        String commonValue = "8080";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, commonValue));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, commonValue));

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod), fullArgs);
        var commonResult = result.get(0);
        var devResult = result.get(1);
        var prodResult = result.get(2);

        assertThat(devResult.getName()).isEqualTo("application-dev.properties");
        assertThat(prodResult.getName()).isEqualTo("application-prod.properties");
        assertThat(commonResult.getName()).isEqualTo("application.properties");

        assertThat(devResult.getLast(commonProperty)).isEmpty();
        assertThat(prodResult.getLast(commonProperty)).isEmpty();
        assertThat(commonResult.getLast(commonProperty)).hasValue(commonValue);
    }

    @Test
    void whenACommonPropertyExistsInProfileFilesButCommonHasDifferentValueThenNoChange() {
        String commonProperty = "server.port";
        String commonValue = "8080";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, commonValue));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, commonValue));
        PropertiesFile common = createFile("application.properties", Map.of(commonProperty, "8888"));

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod, common), fullArgs);
        var commonResult = result.get(0);
        var devResult = result.get(1);
        var prodResult = result.get(2);

        assertThat(devResult.getName()).isEqualTo("application-dev.properties");
        assertThat(prodResult.getName()).isEqualTo("application-prod.properties");
        assertThat(commonResult.getName()).isEqualTo("application.properties");

        assertThat(devResult.getLast(commonProperty)).hasValue(commonValue);
        assertThat(prodResult.getLast(commonProperty)).hasValue(commonValue);
        assertThat(commonResult.getLast(commonProperty)).hasValue("8888");
    }

    @Test
    void whenACommonPropertyExistsInProfileFilesWithDifferentValuesThenNotExtracted() {
        String commonProperty = "server.port";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, "8080"));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, "8081"));

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod), fullArgs);
        var commonResult = result.get(0);
        var devResult = result.get(1);
        var prodResult = result.get(2);

        assertThat(devResult.getName()).isEqualTo("application-dev.properties");
        assertThat(prodResult.getName()).isEqualTo("application-prod.properties");
        assertThat(commonResult.getName()).isEqualTo("application.properties");

        assertThat(devResult.getLast(commonProperty)).hasValue("8080");
        assertThat(prodResult.getLast(commonProperty)).hasValue("8081");
        assertThat(commonResult.getLast(commonProperty)).isEmpty();
    }

    @Test
    void whenACommonPropertyExistsInMultiplePlacesButNotAllThenExtractedInMultipleMode() {
        String commonProperty = "server.port";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, "8080"));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, "8080"));
        PropertiesFile staging = createFile("application-staging.properties", Map.of());

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod,staging ), multipleArgs);
        var commonResult = result.get(0);

        assertThat(commonResult.getLast(commonProperty)).hasValue("8080");
    }

    @Test
    void whenACommonPropertyExistsInMultiplePlacesButNotAllThenNotExtractedInFullMode() {
        String commonProperty = "server.port";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, "8080"));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, "8080"));
        PropertiesFile staging = createFile("application-staging.properties", Map.of());

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod, staging), fullArgs);
        var commonResult = result.get(0);

        assertThat(commonResult.getLast(commonProperty)).isEmpty();
    }

    @Test
    void whenACommonPropertyExistsInMultiplePlacesWithDifferentValuesThenDominantIsPromoted() {
        String commonProperty = "server.port";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, "8080"));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, "8080"));
        PropertiesFile staging = createFile("application-staging.properties", Map.of(commonProperty, "8081"));

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod, staging), multipleArgs);
        var commonResult = result.get(0);
        var devResult = result.get(1);
        var prodResult = result.get(2);
        var stagingResult = result.get(3);

        assertThat(commonResult.getLast(commonProperty)).hasValue("8080");
        assertThat(devResult.getLast(commonProperty)).isEmpty();
        assertThat(prodResult.getLast(commonProperty)).isEmpty();
        assertThat(stagingResult.getLast(commonProperty)).hasValue("8081");
    }

    @Test
    void whenThereIsNoDominantSettingThenNotPromoted() {
        String commonProperty = "server.port";

        PropertiesFile dev = createFile("application-dev.properties", Map.of(commonProperty, "8080"));
        PropertiesFile prod = createFile("application-prod.properties", Map.of(commonProperty, "8080"));
        PropertiesFile staging = createFile("application-staging.properties", Map.of(commonProperty, "8081"));
        PropertiesFile perf = createFile("application-perf.properties", Map.of(commonProperty, "8081"));

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, prod, staging, perf), multipleArgs);
        var commonResult = result.get(0);


        assertThat(commonResult.getLast(commonProperty)).isEmpty();
    }

    @Test
    void whenNoSortingIsOnThenCommonPropertiesFileInNaturalOrder() {
        var someProperties = new LinkedHashMap<String, String>();
        someProperties.put("server.port", "8080");
        someProperties.put("local.host.name", "localhost");
        someProperties.put("local.application", "myApp");

        PropertiesFile dev = createFile("application-dev.properties", someProperties);
        PropertiesFile staging = createFile("application-staging.properties", someProperties);

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, staging), multipleArgs);
        var commonResult = result.get(0);

        assertThat(commonResult.getSettings().stream().map(Setting::getFullPath))
                .containsExactly("server.port", "local.host.name", "local.application");
    }

    @Test
    void whenSortingIsOnThenCommonPropertiesFileGetsSorted() {
        var someProperties = Map.of("server.port", "8080", "local.host.name", "localhost", "local.application", "myApp");
        PropertiesFile dev = createFile("application-dev.properties", someProperties);
        PropertiesFile staging = createFile("application-staging.properties", someProperties);

        multipleArgs.setSort(SpcArgs.SortMode.sorted);

        List<PropertiesFile> result = CommonSettings.process(List.of(dev, staging), multipleArgs);
        var commonResult = result.get(0);

        assertThat(commonResult.getSettings().stream().map(Setting::getFullPath))
                .containsExactly("local.application", "local.host.name", "server.port");
    }

}