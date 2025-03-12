"use client"

import style from "@/app/(afterLogin)/request/dashboard/_component/statistics.module.css"
import React, {useEffect, useState} from "react";
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/_api/getStatisticsRequest";
import type {Statistics} from "@/model/Statistics";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {useSetStatus, useStatus} from "@/app/(afterLogin)/request/dashboard/store/useStatusStore";
import {Status} from "@/model/Status";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics>()
    const globalStatus = useStatus();
    const setStatus = useSetStatus();
    const showAlert = CallAlertDialog();

    useEffect(() => {
        const fetchData = async () => {
            const response = await getStatisticsRequest();
            if (response.ok) {
                const data = await response.json();
                setStatisticsData(data as Statistics);
            }
        };
        fetchData()
    }, []);

    const handleCardOnClick = (status: Status) => {
        setStatus(status);
    };

    const statisticsCards: {
        label: string; value?: number;
        status: Status
    }[] = [
        { label: "Total", value: statisticsData?.total, status: Status.TOTAL },
        { label: "Unconfirmed Order", value: statisticsData?.unconfirmed_order, status: Status.UNCONFIRMED_ORDER },
        { label: "Completed Order", value: statisticsData?.completed_order, status: Status.COMPLETED_ORDER },
        { label: "In progress", value: statisticsData?.in_progress, status: Status.IN_PROGRESS },
        { label: "Test failed", value: statisticsData?.test_failed, status: Status.TEST_FAILED },
        { label: "Delivered", value: statisticsData?.delivered, status: Status.DELIVERED },
        { label: "Completed", value: statisticsData?.completed, status: Status.COMPLETED },
    ];

    const handleLearnMoreClick = (status: Status) => {
        showAlert(`${statusTransferMessage(status)}`);
    };

    const statusTransferMessage = (status: Status) => {
        switch (status) {
            case Status.TOTAL:
                return `This represents the overall status, including all requests regardless of their progress.\nIt serves as a summary of every order, whether it is pending, completed, in progress, or has encountered an issue.`;
            case Status.UNCONFIRMED_ORDER:
                return `This status indicates that the request has been placed but has not yet been fully confirmed or processed.\nIt may still require additional verification, approval, or further input from the requesting party\n before proceeding to the next step.`;
            case Status.COMPLETED_ORDER:
                return `The request has been successfully processed and finalized.\nAll necessary procedures have been carried out,\n and no further action is required.\nThe case is considered closed unless further\n follow-up is needed.`;
            case Status.IN_PROGRESS:
                return `The request is currently undergoing analysis or processing.\nThe required tests are being conducted, and the results have not yet been finalized.`;
            case Status.TEST_FAILED:
                return `This status indicates that the requested analysis\n could not be completed successfully due to an issue. Possible reasons may include sample\n contamination, insufficient sample volume, \n technical errors, or equipment malfunctions.\nAdditional steps may be required, such as repeating\n the test or requesting a new sample.`;
            case Status.DELIVERED:
                return `The test results are currently being sent\n to the requesting party.\nThe delivery process is in progress, but the results have not yet been officially received or confirmed by the recipient.`;
            case Status.COMPLETED:
                return `The final stage of the process, where the test results or reports have been successfully delivered\n and confirmed as received.\nThe request is fully completed, and no further action is needed unless additional follow-up or\n clarification is required.`;
            default:
                return `This represents the overall status, including all requests regardless of their progress.\nIt serves as a summary of every order, whether it is pending, completed, in progress, or has encountered an issue.`;
        }
    };

    return (
        <div className={style.container}>
            <div className={style.titleContainer}>
                <div className={style.mainTitle}>
                    Dashboard
                </div>
            </div>
            <div className={style.cardContainer}>
                {statisticsCards.map((card) => (
                    <div
                        className={`${style.card} ${
                            globalStatus === card.status ? style.activeCard : ""
                        }`}
                         key={card.label}
                         onClick={() => handleCardOnClick(card.status)}
                    >
                        <div className={style.cardLabel}>{card.label}</div>
                        <div className={style.cardValue}>
                            {card.value !== undefined ? card.value : <Loading/>}
                        </div>
                        <div
                            className={style.learnMoreBox}
                            onClick={(e) => {
                                e.stopPropagation();
                                handleLearnMoreClick(card.status);
                            }}
                        >
                            <span className={style.learnMore}>Learn more</span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}