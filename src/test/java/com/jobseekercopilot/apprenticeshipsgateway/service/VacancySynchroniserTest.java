package com.jobseekercopilot.apprenticeshipsgateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jobseekercopilot.apprenticeshipsgateway.client.DfeDisplayAdvertClient;
import com.jobseekercopilot.apprenticeshipsgateway.client.DfeVacancyPage;
import com.jobseekercopilot.apprenticeshipsgateway.client.FixtureApprenticeshipsProviderClient;
import com.jobseekercopilot.apprenticeshipsgateway.config.ApprenticeshipsProperties;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipsSearchRequest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class VacancySynchroniserTest {
    @Test
    void replacesTheSnapshotOnlyAfterAllPagesSucceed() {
        var provider = mock(DfeDisplayAdvertClient.class);
        var fixture = new FixtureApprenticeshipsProviderClient().search(
                new ApprenticeshipsSearchRequest(null, null, null, null, null, null, 1, 20)).jobs().get(0);
        when(provider.fetchPage(1)).thenReturn(new DfeVacancyPage(List.of(fixture), 2, 2, 2));
        when(provider.fetchPage(2)).thenThrow(new IllegalStateException("provider unavailable"));
        var store = new ApprenticeshipVacancyStore();
        store.replace(List.of(), Instant.parse("2026-08-01T00:00:00Z"));

        new VacancySynchroniser(provider, store, new ApprenticeshipsProperties()).synchronise();

        assertThat(store.snapshot().vacancies()).isEmpty();
        assertThat(store.snapshot().updatedAt()).isEqualTo(Instant.parse("2026-08-01T00:00:00Z"));
    }
}
