package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.model.DistributionChannel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DistributionChannelManager {

    @Value("${bonify.distribution.channel.tenant_ids}")
    private Set<String> bonifyDistributionChannelTenantIds;

    public DistributionChannel extractDistributionChannel(String tenantId) {
        if (bonifyDistributionChannelTenantIds.contains(tenantId)) {
            return DistributionChannel.BONIFY;
        }
        return DistributionChannel.OTHERS;
    }
}
