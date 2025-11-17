package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final BouncycastleLibraryAccessors laccForBouncycastleLibraryAccessors = new BouncycastleLibraryAccessors(owner);
    private final JacksonLibraryAccessors laccForJacksonLibraryAccessors = new JacksonLibraryAccessors(owner);
    private final JjwtLibraryAccessors laccForJjwtLibraryAccessors = new JjwtLibraryAccessors(owner);
    private final JooqLibraryAccessors laccForJooqLibraryAccessors = new JooqLibraryAccessors(owner);
    private final KotestLibraryAccessors laccForKotestLibraryAccessors = new KotestLibraryAccessors(owner);
    private final KotlinLibraryAccessors laccForKotlinLibraryAccessors = new KotlinLibraryAccessors(owner);
    private final KubernetesLibraryAccessors laccForKubernetesLibraryAccessors = new KubernetesLibraryAccessors(owner);
    private final R2dbcLibraryAccessors laccForR2dbcLibraryAccessors = new R2dbcLibraryAccessors(owner);
    private final ReactorLibraryAccessors laccForReactorLibraryAccessors = new ReactorLibraryAccessors(owner);
    private final SpringLibraryAccessors laccForSpringLibraryAccessors = new SpringLibraryAccessors(owner);
    private final TestcontainersLibraryAccessors laccForTestcontainersLibraryAccessors = new TestcontainersLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>cassandra</b> with <b>org.springframework.boot:spring-boot-starter-data-cassandra</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in settings file 'settings.gradle.kts'
     */
    public Provider<MinimalExternalModuleDependency> getCassandra() {
        return create("cassandra");
    }

    /**
     * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
     * with version <b>1.18.26</b>
     * <p>
     * This dependency was declared in settings file 'settings.gradle.kts'
     */
    public Provider<MinimalExternalModuleDependency> getLombok() {
        return create("lombok");
    }

    /**
     * Dependency provider for <b>mockk</b> with <b>io.mockk:mockk</b> coordinates and
     * with version <b>1.13.10</b>
     * <p>
     * This dependency was declared in settings file 'settings.gradle.kts'
     */
    public Provider<MinimalExternalModuleDependency> getMockk() {
        return create("mockk");
    }

    /**
     * Dependency provider for <b>reflect</b> with <b>org.jetbrains.kotlin:kotlin-reflect</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in settings file 'settings.gradle.kts'
     */
    public Provider<MinimalExternalModuleDependency> getReflect() {
        return create("reflect");
    }

    /**
     * Group of libraries at <b>bouncycastle</b>
     */
    public BouncycastleLibraryAccessors getBouncycastle() {
        return laccForBouncycastleLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jackson</b>
     */
    public JacksonLibraryAccessors getJackson() {
        return laccForJacksonLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jjwt</b>
     */
    public JjwtLibraryAccessors getJjwt() {
        return laccForJjwtLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jooq</b>
     */
    public JooqLibraryAccessors getJooq() {
        return laccForJooqLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kotest</b>
     */
    public KotestLibraryAccessors getKotest() {
        return laccForKotestLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kotlin</b>
     */
    public KotlinLibraryAccessors getKotlin() {
        return laccForKotlinLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kubernetes</b>
     */
    public KubernetesLibraryAccessors getKubernetes() {
        return laccForKubernetesLibraryAccessors;
    }

    /**
     * Group of libraries at <b>r2dbc</b>
     */
    public R2dbcLibraryAccessors getR2dbc() {
        return laccForR2dbcLibraryAccessors;
    }

    /**
     * Group of libraries at <b>reactor</b>
     */
    public ReactorLibraryAccessors getReactor() {
        return laccForReactorLibraryAccessors;
    }

    /**
     * Group of libraries at <b>spring</b>
     */
    public SpringLibraryAccessors getSpring() {
        return laccForSpringLibraryAccessors;
    }

    /**
     * Group of libraries at <b>testcontainers</b>
     */
    public TestcontainersLibraryAccessors getTestcontainers() {
        return laccForTestcontainersLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class BouncycastleLibraryAccessors extends SubDependencyFactory {

        public BouncycastleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>bcprov</b> with <b>org.bouncycastle:bcprov-jdk18on</b> coordinates and
         * with version <b>1.77</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getBcprov() {
            return create("bouncycastle.bcprov");
        }

    }

    public static class JacksonLibraryAccessors extends SubDependencyFactory {
        private final JacksonDatatypeLibraryAccessors laccForJacksonDatatypeLibraryAccessors = new JacksonDatatypeLibraryAccessors(owner);

        public JacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>annotations</b> with <b>com.fasterxml.jackson.core:jackson-annotations</b> coordinates and
         * with version <b>2.14.2</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getAnnotations() {
            return create("jackson.annotations");
        }

        /**
         * Group of libraries at <b>jackson.datatype</b>
         */
        public JacksonDatatypeLibraryAccessors getDatatype() {
            return laccForJacksonDatatypeLibraryAccessors;
        }

    }

    public static class JacksonDatatypeLibraryAccessors extends SubDependencyFactory {

        public JacksonDatatypeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jsr310</b> with <b>com.fasterxml.jackson.datatype:jackson-datatype-jsr310</b> coordinates and
         * with version <b>2.14.2</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getJsr310() {
            return create("jackson.datatype.jsr310");
        }

    }

    public static class JjwtLibraryAccessors extends SubDependencyFactory {

        public JjwtLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>io.jsonwebtoken:jjwt-api</b> coordinates and
         * with version <b>0.12.5</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("jjwt.api");
        }

        /**
         * Dependency provider for <b>impl</b> with <b>io.jsonwebtoken:jjwt-impl</b> coordinates and
         * with version <b>0.12.5</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getImpl() {
            return create("jjwt.impl");
        }

        /**
         * Dependency provider for <b>jackson</b> with <b>io.jsonwebtoken:jjwt-jackson</b> coordinates and
         * with version <b>0.12.5</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getJackson() {
            return create("jjwt.jackson");
        }

    }

    public static class JooqLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public JooqLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jooq</b> with <b>org.jooq:jooq</b> coordinates and
         * with version <b>3.19.0</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("jooq");
        }

        /**
         * Dependency provider for <b>codegen</b> with <b>org.jooq:jooq-codegen</b> coordinates and
         * with version <b>3.19.0</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getCodegen() {
            return create("jooq.codegen");
        }

        /**
         * Dependency provider for <b>meta</b> with <b>org.jooq:jooq-meta</b> coordinates and
         * with version <b>3.19.0</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getMeta() {
            return create("jooq.meta");
        }

        /**
         * Dependency provider for <b>r2dbc</b> with <b>org.jooq:jooq-r2dbc</b> coordinates and
         * with version <b>3.19.0</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getR2dbc() {
            return create("jooq.r2dbc");
        }

    }

    public static class KotestLibraryAccessors extends SubDependencyFactory {
        private final KotestExtensionsLibraryAccessors laccForKotestExtensionsLibraryAccessors = new KotestExtensionsLibraryAccessors(owner);

        public KotestLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>runner</b> with <b>io.kotest:kotest-runner-junit5</b> coordinates and
         * with version <b>5.8.1</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getRunner() {
            return create("kotest.runner");
        }

        /**
         * Group of libraries at <b>kotest.extensions</b>
         */
        public KotestExtensionsLibraryAccessors getExtensions() {
            return laccForKotestExtensionsLibraryAccessors;
        }

    }

    public static class KotestExtensionsLibraryAccessors extends SubDependencyFactory {

        public KotestExtensionsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>spring</b> with <b>io.kotest.extensions:kotest-extensions-spring</b> coordinates and
         * with version <b>1.1.3</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getSpring() {
            return create("kotest.extensions.spring");
        }

        /**
         * Dependency provider for <b>testcontainers</b> with <b>io.kotest.extensions:kotest-extensions-testcontainers</b> coordinates and
         * with version <b>2.0.2</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getTestcontainers() {
            return create("kotest.extensions.testcontainers");
        }

    }

    public static class KotlinLibraryAccessors extends SubDependencyFactory {
        private final KotlinCoroutinesLibraryAccessors laccForKotlinCoroutinesLibraryAccessors = new KotlinCoroutinesLibraryAccessors(owner);

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jackson</b> with <b>com.fasterxml.jackson.module:jackson-module-kotlin</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getJackson() {
            return create("kotlin.jackson");
        }

        /**
         * Dependency provider for <b>reactor</b> with <b>io.projectreactor.kotlin:reactor-kotlin-extensions</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getReactor() {
            return create("kotlin.reactor");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.jetbrains.kotlin:kotlin-test</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("kotlin.test");
        }

        /**
         * Group of libraries at <b>kotlin.coroutines</b>
         */
        public KotlinCoroutinesLibraryAccessors getCoroutines() {
            return laccForKotlinCoroutinesLibraryAccessors;
        }

    }

    public static class KotlinCoroutinesLibraryAccessors extends SubDependencyFactory {

        public KotlinCoroutinesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>reactor</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-reactor</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getReactor() {
            return create("kotlin.coroutines.reactor");
        }

    }

    public static class KubernetesLibraryAccessors extends SubDependencyFactory {

        public KubernetesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>mock</b> with <b>io.fabric8:kubernetes-server-mock</b> coordinates and
         * with version <b>6.10.0</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getMock() {
            return create("kubernetes.mock");
        }

    }

    public static class R2dbcLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public R2dbcLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>r2dbc</b> with <b>org.springframework.boot:spring-boot-starter-data-r2dbc</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("r2dbc");
        }

        /**
         * Dependency provider for <b>postgres</b> with <b>org.postgresql:r2dbc-postgresql</b> coordinates and
         * with version <b>1.0.0.RELEASE</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getPostgres() {
            return create("r2dbc.postgres");
        }

    }

    public static class ReactorLibraryAccessors extends SubDependencyFactory {

        public ReactorLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>test</b> with <b>io.projectreactor:reactor-test</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("reactor.test");
        }

    }

    public static class SpringLibraryAccessors extends SubDependencyFactory {
        private final SpringBootLibraryAccessors laccForSpringBootLibraryAccessors = new SpringBootLibraryAccessors(owner);
        private final SpringCloudLibraryAccessors laccForSpringCloudLibraryAccessors = new SpringCloudLibraryAccessors(owner);
        private final SpringSecurityLibraryAccessors laccForSpringSecurityLibraryAccessors = new SpringSecurityLibraryAccessors(owner);

        public SpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>actuator</b> with <b>org.springframework.boot:spring-boot-starter-actuator</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getActuator() {
            return create("spring.actuator");
        }

        /**
         * Dependency provider for <b>gateway</b> with <b>org.springframework.cloud:spring-cloud-starter-gateway</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getGateway() {
            return create("spring.gateway");
        }

        /**
         * Dependency provider for <b>hateoas</b> with <b>org.springframework.hateoas:spring-hateoas</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getHateoas() {
            return create("spring.hateoas");
        }

        /**
         * Dependency provider for <b>kafka</b> with <b>org.springframework.cloud:spring-cloud-starter-stream-kafka</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getKafka() {
            return create("spring.kafka");
        }

        /**
         * Dependency provider for <b>log4j2</b> with <b>org.springframework.boot:spring-boot-starter-log4j2</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getLog4j2() {
            return create("spring.log4j2");
        }

        /**
         * Dependency provider for <b>validation</b> with <b>org.springframework.boot:spring-boot-starter-validation</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getValidation() {
            return create("spring.validation");
        }

        /**
         * Dependency provider for <b>webflux</b> with <b>org.springframework.boot:spring-boot-starter-webflux</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getWebflux() {
            return create("spring.webflux");
        }

        /**
         * Group of libraries at <b>spring.boot</b>
         */
        public SpringBootLibraryAccessors getBoot() {
            return laccForSpringBootLibraryAccessors;
        }

        /**
         * Group of libraries at <b>spring.cloud</b>
         */
        public SpringCloudLibraryAccessors getCloud() {
            return laccForSpringCloudLibraryAccessors;
        }

        /**
         * Group of libraries at <b>spring.security</b>
         */
        public SpringSecurityLibraryAccessors getSecurity() {
            return laccForSpringSecurityLibraryAccessors;
        }

    }

    public static class SpringBootLibraryAccessors extends SubDependencyFactory {

        public SpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.boot:spring-boot-starter-test</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("spring.boot.test");
        }

    }

    public static class SpringCloudLibraryAccessors extends SubDependencyFactory {

        public SpringCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>bom</b> with <b>org.springframework.cloud:spring-cloud-dependencies</b> coordinates and
         * with version <b>2023.0.0</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getBom() {
            return create("spring.cloud.bom");
        }

    }

    public static class SpringSecurityLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public SpringSecurityLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>security</b> with <b>org.springframework.boot:spring-boot-starter-security</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("spring.security");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.security:spring-security-test</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("spring.security.test");
        }

    }

    public static class TestcontainersLibraryAccessors extends SubDependencyFactory {

        public TestcontainersLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>junit</b> with <b>org.testcontainers:junit-jupiter</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getJunit() {
            return create("testcontainers.junit");
        }

        /**
         * Dependency provider for <b>postgresql</b> with <b>org.testcontainers:postgresql</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in settings file 'settings.gradle.kts'
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("testcontainers.postgresql");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

    public static class BundleAccessors extends BundleFactory {
        private final JjwtBundleAccessors baccForJjwtBundleAccessors = new JjwtBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
        private final KotlinBundleAccessors baccForKotlinBundleAccessors = new KotlinBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
        private final R2dbcBundleAccessors baccForR2dbcBundleAccessors = new R2dbcBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
        private final SpringBundleAccessors baccForSpringBundleAccessors = new SpringBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
        private final TestBundleAccessors baccForTestBundleAccessors = new TestBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>jooq</b> which contains the following dependencies:
         * <ul>
         *    <li>org.jooq:jooq</li>
         *    <li>org.jooq:jooq-codegen</li>
         *    <li>org.jooq:jooq-meta</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getJooq() {
            return createBundle("jooq");
        }

        /**
         * Group of bundles at <b>bundles.jjwt</b>
         */
        public JjwtBundleAccessors getJjwt() {
            return baccForJjwtBundleAccessors;
        }

        /**
         * Group of bundles at <b>bundles.kotlin</b>
         */
        public KotlinBundleAccessors getKotlin() {
            return baccForKotlinBundleAccessors;
        }

        /**
         * Group of bundles at <b>bundles.r2dbc</b>
         */
        public R2dbcBundleAccessors getR2dbc() {
            return baccForR2dbcBundleAccessors;
        }

        /**
         * Group of bundles at <b>bundles.spring</b>
         */
        public SpringBundleAccessors getSpring() {
            return baccForSpringBundleAccessors;
        }

        /**
         * Group of bundles at <b>bundles.test</b>
         */
        public TestBundleAccessors getTest() {
            return baccForTestBundleAccessors;
        }

    }

    public static class JjwtBundleAccessors extends BundleFactory {

        public JjwtBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>jjwt.runtime</b> which contains the following dependencies:
         * <ul>
         *    <li>io.jsonwebtoken:jjwt-impl</li>
         *    <li>io.jsonwebtoken:jjwt-jackson</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getRuntime() {
            return createBundle("jjwt.runtime");
        }

    }

    public static class KotlinBundleAccessors extends BundleFactory  implements BundleNotationSupplier{

        public KotlinBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>kotlin</b> which contains the following dependencies:
         * <ul>
         *    <li>org.jetbrains.kotlin:kotlin-reflect</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> asProvider() {
            return createBundle("kotlin");
        }

        /**
         * Dependency bundle provider for <b>kotlin.webflux</b> which contains the following dependencies:
         * <ul>
         *    <li>org.springframework.boot:spring-boot-starter-webflux</li>
         *    <li>org.jetbrains.kotlin:kotlin-reflect</li>
         *    <li>io.projectreactor.kotlin:reactor-kotlin-extensions</li>
         *    <li>org.jetbrains.kotlinx:kotlinx-coroutines-reactor</li>
         *    <li>com.fasterxml.jackson.module:jackson-module-kotlin</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getWebflux() {
            return createBundle("kotlin.webflux");
        }

    }

    public static class R2dbcBundleAccessors extends BundleFactory {

        public R2dbcBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>r2dbc.postgres</b> which contains the following dependencies:
         * <ul>
         *    <li>org.springframework.boot:spring-boot-starter-data-r2dbc</li>
         *    <li>org.postgresql:r2dbc-postgresql</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getPostgres() {
            return createBundle("r2dbc.postgres");
        }

    }

    public static class SpringBundleAccessors extends BundleFactory {

        public SpringBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>spring.client</b> which contains the following dependencies:
         * <ul>
         *    <li>org.springframework.boot:spring-boot-starter-log4j2</li>
         *    <li>org.springframework.boot:spring-boot-starter-security</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getClient() {
            return createBundle("spring.client");
        }

    }

    public static class TestBundleAccessors extends BundleFactory  implements BundleNotationSupplier{

        public TestBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>test</b> which contains the following dependencies:
         * <ul>
         *    <li>org.springframework.boot:spring-boot-starter-test</li>
         *    <li>io.mockk:mockk</li>
         *    <li>io.projectreactor:reactor-test</li>
         *    <li>org.jetbrains.kotlin:kotlin-test</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> asProvider() {
            return createBundle("test");
        }

        /**
         * Dependency bundle provider for <b>test.api</b> which contains the following dependencies:
         * <ul>
         *    <li>io.projectreactor:reactor-test</li>
         *    <li>io.kotest:kotest-runner-junit5</li>
         *    <li>io.mockk:mockk</li>
         *    <li>io.kotest.extensions:kotest-extensions-spring</li>
         *    <li>org.springframework.boot:spring-boot-starter-test</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getApi() {
            return createBundle("test.api");
        }

        /**
         * Dependency bundle provider for <b>test.containers</b> which contains the following dependencies:
         * <ul>
         *    <li>org.testcontainers:junit-jupiter</li>
         *    <li>org.testcontainers:postgresql</li>
         *    <li>io.kotest.extensions:kotest-extensions-testcontainers</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getContainers() {
            return createBundle("test.containers");
        }

        /**
         * Dependency bundle provider for <b>test.kubernetes</b> which contains the following dependencies:
         * <ul>
         *    <li>io.fabric8:kubernetes-server-mock</li>
         * </ul>
         * <p>
         * This bundle was declared in settings file 'settings.gradle.kts'
         */
        public Provider<ExternalModuleDependencyBundle> getKubernetes() {
            return createBundle("test.kubernetes");
        }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
