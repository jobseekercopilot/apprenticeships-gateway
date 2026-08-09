package com.jobseekercopilot.apprenticeshipsgateway.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipsSearchRequest;
import com.jobseekercopilot.apprenticeshipsgateway.service.ApprenticeshipVacancyStore;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CachedApprenticeshipsProviderClientTest {
    @Test
    void searchesTheLocalSnapshotAndRetainsMultipleLocations() {
        var fixture = new FixtureApprenticeshipsProviderClient().search(
                new ApprenticeshipsSearchRequest(null, null, null, null, null, null, 1, 20));
        var store = new ApprenticeshipVacancyStore();
        store.replace(fixture.jobs(), Instant.now());
        var client = new CachedApprenticeshipsProviderClient(store);

        var result = client.search(new ApprenticeshipsSearchRequest(
                "developer", "Bradford", 10, null, null, null, 1, 20));

        assertThat(result.jobs()).singleElement().satisfies(job -> {
            assertThat(job.vacancyReference()).isEqualTo("VAC1000001");
            assertThat(job.addresses()).hasSize(2);
            assertThat(job.courseTitle()).isEqualTo("Software developer (level 4)");
        });
    }

    @Test
    void distanceSearchMatchesAnyAdvertisedLocation() {
        var fixture = new FixtureApprenticeshipsProviderClient().search(
                new ApprenticeshipsSearchRequest(null, null, null, null, null, null, 1, 20));
        var store = new ApprenticeshipVacancyStore();
        store.replace(fixture.jobs(), Instant.now());

        var result = new CachedApprenticeshipsProviderClient(store).search(
                new ApprenticeshipsSearchRequest("developer", null, 2,
                        new BigDecimal("53.7950"), new BigDecimal("-1.7594"), null, 1, 20));

        assertThat(result.jobs()).hasSize(1);
    }
}
