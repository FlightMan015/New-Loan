package de.joonko.loan.db.repositories;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

@Repository
public interface LoanOfferStoreRepository extends CrudRepository<LoanOfferStore, String> {

    List<LoanOfferStore> findAllByApplicationId(String loanApplicationId);

    List<LoanOfferStore> findAllByLoanProviderReferenceNumber(final String loanProviderReferenceNumber);

    List<LoanOfferStore> findAllByLoanProviderReferenceNumberAndOfferDurationInMonthAndOfferLoanProviderName(final String loanProviderReferenceNumber, int durationInMonths, @NotNull final String loanProviderName);

    @Query("{ 'deleted': { '$exists': false }, " +
            "'userUUID': ?0, " +
            "'$or': [{'applicationId': ?1}, {'parentApplicationId': '?1'}]    " +
            "} ")
    List<LoanOfferStore> getNotDeletedOffers(String userUUID, String applicationId);


    @Query("{ 'deleted': { '$exists': false }, " +
            "'userUUID': ?0, " +
            "'$or': [{'applicationId': ?1}, {'parentApplicationId': '?1'}], " +
            "'offer.loanProvider.name': {$in: ?2}" +
            "} ")
    List<LoanOfferStore> getByUserIdApplicationIdAndProviders(String userUUID, String applicationId, Set<String> loanProviders);

    List<LoanOfferStore> deleteByUserUUID(final String userUUID);

    @Query("{" +
            "    'offer.loanProvider.name': {$in: ?0}," +
            "'isAccepted': true," +
            "$or: [{'offerStatus': {$exists: false}}, {'offerStatus': {$in: ?1}} ]" +
            "}")
    List<LoanOfferStore> findByOfferStatusAndLoanProvider(Set<Bank> loanProviders, Set<LoanApplicationStatus> offerStatuses);

    @Query("{" +
            "    'isAccepted': true, " +
            "    'applicationId': ?0, " +
            "    'offer.loanProvider.name': ?1" +
            "}")
    List<LoanOfferStore> findByApplicationIdAndIsAcceptedAndLoanProvider(String applicationId, String loanProvider);

    List<LoanOfferStore> findLoanOfferStoreByUserUUIDAndContractsIsNotNull(String userUuid);

    @Aggregation(pipeline = {
            "{" +
                    "        $match: {" +
                    "            $or: [" +
                    "                {" +
                    "                    lastModifiedTS: {" +
                    "                        $gte: ?1" +
                    "                    }" +
                    "                }, {" +
                    "                    $and: [" +
                    "                        {" +
                    "                            lastModifiedTS: {" +
                    "                                $gte: ?0," +
                    "                                $lte: ?1" +
                    "                            }" +
                    "                        }, {$or: [{" +
                    "                            kycStatus: {$in: ?2}" +
                    "                        }, {" +
                    "                            offerStatus: {$in: ?2}" +
                    "                        }]}" +
                    "                    ]" +
                    "                }" +
                    "            ]" +
                    "        }" +
                    "    }",
            "{" +
                    "        $group: {" +
                    "            _id: '$userUUID'," +
                    "            offer: {$first: '$$ROOT'}" +
                    "        }" +
                    "    }",
            "{" +
                    "        $replaceRoot: {" +
                    "            newRoot: '$offer'" +
                    "        }" +
                    "    }"
    })
    List<LoanOfferStore> getAnyOfferForEachActiveUser(LocalDateTime startDate, LocalDateTime endDate, Set<String> finiteStatuses);

    @Aggregation(pipeline = {
            "{" +
                    "        $match: {" +
                    "            lastModifiedTS: {" +
                    "                $gte: ?0," +
                    "                $lte: ?1" +
                    "            }," +
                    "            userUUID: {$nin: ?2}" +
                    "        }," +
                    "    }",
            "{" +
                    "        $group: {" +
                    "            _id: '$userUUID'," +
                    "            offer: {$first: '$$ROOT'}" +
                    "        }" +
                    "    }",
            "{" +
                    "        $replaceRoot: {" +
                    "            newRoot: '$offer'" +
                    "        }" +
                    "    }"
    })
    List<LoanOfferStore> getAnyOfferForEachAbandonedUser(LocalDateTime startDate, LocalDateTime endDate, Set<String> activeUserUuids);

    @Aggregation(pipeline = {
            "{" +
                    "        $match: {" +
                    "            applicationId: {$in: ?0}" +
                    "        }," +
                    "    }",
            "{" +
                    "        $sort: {" +
                    "            statusLastUpdateDate: -1," +
                    "            kycStatusLastUpdateDate: -1," +
                    "            acceptedDate: -1," +
                    "            insertTS: -1" +
                    "        }" +
                    "    }",
            "{" +
                    "        $group: {" +
                    "            _id: '$applicationId'," +
                    "            offer: {$first: '$$ROOT'}" +
                    "        }" +
                    "    }",
            "{" +
                    "        $replaceRoot: {" +
                    "            newRoot: '$offer'" +
                    "        }" +
                    "    }"
    })
    List<LoanOfferStore> getLatestUpdatedOffersForEachApplication(Set<String> applicationIds);
}
