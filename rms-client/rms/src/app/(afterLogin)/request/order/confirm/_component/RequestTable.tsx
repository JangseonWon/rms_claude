"use client"

import globalTableScrollStyle from "@/css/globalTableScroll.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {postRequests} from "@/app/(afterLogin)/request/order/_api/postRequests";
import {FilterGroup, Query} from "@/model/Query";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {Filter} from "@/model/Filter";
import {Status} from "@/model/Status";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import BlueButton from "@/app/_component/BlueButton";
import GreenButton from "@/app/_component/GreenButton";
import AirWaybillModal from "@/app/(afterLogin)/request/order/confirm/_component/AirWaybillModal";
import {patchRequests} from "@/app/(afterLogin)/request/order/_api/patchRequests";
import RequestInfo from "@/app/(afterLogin)/request/order/_component/RequestInfo";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

export interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "request", column: "courier_company", name: "Global courier"},
    { table: "request", column: "awb_number", name: "AirWaybill no"},
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" },
];
const defaultFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.UNCONFIRMED_ORDER.valueOf()
}

export default function RequestTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [airWaybillModal, setAirWaybillModal] = useState<boolean>(false);
    const [selectedRequests, setSelectedRequests] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData.every((row) => row.isSelected);
    const [searchFilter, setSearchFilter] = useState<Filter | null>(null);
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>();
    const [infoModalOpen, setInfoModalOpen] = useState<boolean>(false);
    const [infoRequest, setInfoRequest] = useState<Request>();
    const showAlert = CallAlertDialog();

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };
    const handleAirWaybillClick = () => {
        const selected = requestData.filter((request) => request.isSelected);
        setSelectedRequests(selected);
        setAirWaybillModal(true);
    }
    const handleConfirmClick = async () => {
        const selected = requestData.filter((request) => request.isSelected);
        const hasMissingInfo = selected.some(
            (request) => !request.awb_number || !request.courier_company
        );

        if (hasMissingInfo) {
            alert("누락된 정보가 있습니다. AirWaybill 번호와 Global courier 정보를 모두 입력해주세요.");
            return;
        }

        const updatedRequests = selected.map((request) => ({
            sample: { id: request.sample?.id },
            service: { id: request.service?.id },
            status: Status.COMPLETED_ORDER.valueOf(),
        }));

        try {
            const response = await patchRequests(updatedRequests);
            if(response.ok) {
                showAlert("Success")
                await fetchData(updatedSearch());
            } else {
                console.error("Failed to confirm requests:", response.statusText);
            }
        } catch (error) {
            console.error("Failed to confirm requests:", error);
        }
    };

    const closeModal = () => {
        setInfoModalOpen(false);
        setAirWaybillModal(false);
    }

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) => prevData.map((row) => ({ ...row, isSelected })));
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search);
            const data = await response.json();
            setRequestData(data as Request[]);
        }
        catch {
            setRequestData([]);
        }
    };

    const updatedSearch = (): Query => ({
        filter_groups: [
            ...(orderDateFilter ? [orderDateFilter] : []),
            {
                filters: [
                    defaultFilter,
                    ...(searchFilter ? [searchFilter] : []),
                ],
            },
        ],
    });

    const handleInfoClick = (request: RequestWithSelected) => {
        setInfoRequest(request);
        setInfoModalOpen(true);
    };

    useEffect(() => {
        fetchData(updatedSearch());
    }, [searchFilter,orderDateFilter]);

    return (
        <>
            <div className={globalTableScrollStyle.formGroupRight}>
                <GreenButton name={'Input AirWaybill'} onClick={handleAirWaybillClick}/>
                <BlueButton name={'Confirm'} onClick={handleConfirmClick}/>
            </div>
            <div className={globalTableScrollStyle.formGroupBetween}>
                <div>
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) => {
                            setOrderDateFilter(
                                from && to ? {
                                    filters: [
                                        {
                                            table: "request",
                                            column: "create_at",
                                            value: from?.toLocaleDateString('en-CA'),
                                            operator: ">="
                                        },
                                        {
                                            table: "request",
                                            column: "create_at",
                                            value: to.toLocaleDateString('en-CA'),
                                            operator: "<="
                                        }
                                    ]
                                } as FilterGroup : undefined
                            )
                        }}/>
                </div>
                <div>
                    <SelectBox
                        width={"200px"}
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                        }}
                    />
                    <InputBox
                        label={"search"}
                        onChange={(value) => {
                            setSearchFilter(
                                value && value.trim() !== ""
                                    ? {
                                        table: selectedOption.table,
                                        column: selectedOption.column,
                                        operator: "LIKE",
                                        value: value
                                    } as Filter
                                    : null
                            );
                    }}></InputBox>
                </div>
            </div>
            <table className={globalTableScrollStyle.table}>
                <thead>
                <tr>
                    <th>
                        <label form="agree" className={globalTableScrollStyle.checkbox}>
                            <input
                                type="checkbox"
                                checked={isSelectedAll}
                                onChange={() => handleSelectAll(!isSelectedAll)}
                                className={globalTableScrollStyle.checkbox}
                            />
                            <span className={globalTableScrollStyle.checkmark}></span>
                        </label>
                    </th>
                    <th>Global courier</th>
                    <th>AirWaybill no.</th>
                    <th>Order Date<br/>(DD-MM-YYYY)</th>
                    <th>User Name</th>
                    <th>Institution</th>
                    <th>Registration ID</th>
                    <th>Service</th>
                    <th>Patient(s) Name</th>
                    <th>Patient(s) DOB<br/>(DD-MM-YYYY)</th>
                    <th>MRN</th>
                    <th>Info</th>
                </tr>
                </thead>
                <tbody>
                {requestData && requestData.length > 0 && requestData.map((request, rowIndex) => (
                    <tr key={request.order_id! + request.service!.id + request.sample!.id}>
                        <td onClick={(e) => e.stopPropagation()}>
                            <label form="agree" className={globalTableScrollStyle.checkbox}>
                                <input
                                    type="checkbox"
                                    checked={request.isSelected || false}
                                    onChange={() => handleSelectChange(rowIndex, !request.isSelected)}
                                    className={globalTableScrollStyle.checkbox}
                                />
                                <span className={globalTableScrollStyle.checkmark}></span>
                            </label>
                        </td>
                        <td>{request.courier_company}</td>
                        <td>{request.awb_number}</td>
                        <td>{request.create_at ? new Date(request.create_at).toLocaleDateString('en-GB').replace(/\//g, '-') : ''}</td>
                        <td>{request.order?.user?.name}</td>
                        <td>{request.sample?.patient?.organization?.name}</td>
                        <td>{request.sample?.barcode}</td>
                        <td>{request.service?.name}</td>
                        <td>{request.sample?.patient?.name}</td>
                        <td>{request.sample?.patient?.birth_day}-{request.sample?.patient?.birth_month}-{request.sample?.patient?.birth_year}</td>
                        <td>{request.sample?.patient?.serial}</td>
                        <td>
                            <FontAwesomeIcon
                                icon={faFileLines}
                                className={globalTableScrollStyle.info}
                                onClick={(e) => {
                                    handleInfoClick(request)
                                    e.stopPropagation();
                                }}/>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {infoModalOpen && (
                <RequestInfo
                    serviceId={infoRequest?.service!.id!}
                    sampleId={infoRequest?.sample!.id!}
                    closeModal={closeModal}
                />
            )}
            {airWaybillModal && (
                <AirWaybillModal
                    closeModal={closeModal}
                    selectedRequests={selectedRequests}
                    onConfirm={async (courierCompany, awbNumber) => {
                        const updatedRequests: Request[] = selectedRequests.map((request) => ({
                            sample: {id: request.sample?.id},
                            service: {id: request.service?.id},
                            courier_company: courierCompany,
                            awb_number: awbNumber,
                        }));
                        const response = await patchRequests(updatedRequests);
                        closeModal();

                        if (response.ok) {
                            showAlert("Success");
                            closeModal();
                            await fetchData(updatedSearch());
                        } else {showAlert("Fail");}
                    }}
                />
            )}
        </>
    )
}