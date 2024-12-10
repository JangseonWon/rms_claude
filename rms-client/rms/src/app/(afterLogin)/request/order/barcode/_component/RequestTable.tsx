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
import BlueButton from "@/app/_component/BlueButton";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import RequestInfo from "@/app/(afterLogin)/request/order/_component/RequestInfo";
import BarcodeModal from "@/app/(afterLogin)/request/order/barcode/_component/BarcodeModal";

export interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "organization", column: "name", name: "Institution" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "sex", name: "Gender" },
    { table: "patient", column: "serial", name: "MRN" },
];
const defaultFilter: Filter = {
    table: "request",
    column: "status",
    operator: "!=",
    value: Status.CART.valueOf()
}
const today = new Date();
const sevenDaysAgo = new Date();
sevenDaysAgo.setDate(today.getDate() - 7);
const defaultOrderDateFilter: FilterGroup = {
    filters: [
        {
            table: "request",
            column: "create_at",
            value: sevenDaysAgo.toLocaleDateString('en-CA'),
            operator: ">="
        },
        {
            table: "request",
            column: "create_at",
            value: today.toLocaleDateString('en-CA'),
            operator: "<="
        }
    ]
}

export default function RequestTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const isSelectedAll = requestData.every((row) => row.isSelected);
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [barcodeModalOpen, setBarcodeModalOpen] = useState<boolean>(false);
    const [infoRequest, setInfoRequest] = useState<Request>();
    const [searchFilter, setSearchFilter] = useState<Filter | null>(null)
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>(defaultOrderDateFilter)

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
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

    const handleBarcodeClick = () => {
        setBarcodeModalOpen(true)
    }

    const handleInfoClick = (row: RequestWithSelected) => {
        setInfoRequest(row);
        setModalOpen(true);
    };

    const closeModal = () => {
        setInfoRequest(undefined);
        setModalOpen(false);
        setBarcodeModalOpen(false);
    }

    const selectedRequest = requestData.filter((row) => row.isSelected);

    useEffect(() => {
        const updatedSearch = {
            filter_groups: [
                ...(orderDateFilter ? [orderDateFilter] : []),
                {
                    filters: [
                        defaultFilter,
                        ...(searchFilter ? [searchFilter] : []),
                    ],
                },
            ],
        };
        fetchData(updatedSearch);
    }, [searchFilter,orderDateFilter]);

    return (
        <>
            <div className={globalTableScrollStyle.formGroupRight}>
                <BlueButton name={'Print Barcode'} onClick={handleBarcodeClick}/>
            </div>
            <div className={globalTableScrollStyle.formGroupBetween}>
                <div>
                    <DatePickerRangeBox
                        label={"from-to"}
                        fromDate={sevenDaysAgo}
                        toDate={today}
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
                                } as FilterGroup : defaultOrderDateFilter
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
                            setSearchFilter({
                                table: option.table!,
                                column: option.column!
                            } as Filter)
                        }}
                    />
                    <InputBox label={"search"} onChange={(value) => {
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
                    <th>Order Date<br/>(DD-MM-YYYY)</th>
                    <th>User Name</th>
                    <th>Institution</th>
                    <th>Registration ID</th>
                    <th>Patient(s) Name</th>
                    <th>Service</th>
                    <th>Patient(s) DOB<br/>(DD-MM-YYYY)</th>
                    <th>MRN</th>
                    <th>Info</th>
                </tr>
                </thead>
                <tbody style={{height: "300px"}}>
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
                        <td>{request.create_at ? new Date(request.create_at).toLocaleDateString('en-GB').replace(/\//g, '-') : ''}</td>
                        <td>userName</td>
                        <td>{request.sample?.patient?.organization?.name}</td>
                        <td>{request.sample?.barcode}</td>
                        <td>{request.sample?.patient?.name}</td>
                        <td>{request.service?.name}</td>
                        <td>{request.sample?.patient?.birth_day}-{request.sample?.patient?.birth_month}-{request.sample?.patient?.birth_year}</td>
                        <td>{request.sample?.patient?.serial}</td>
                        <td>
                            <FontAwesomeIcon
                                icon={faFileLines}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleInfoClick(request);
                                }}/>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {modalOpen && (
                <RequestInfo
                    serviceId={infoRequest?.service!.id!}
                    sampleId={infoRequest?.sample!.id!}
                    closeModal={closeModal}
                />
            )}
            {barcodeModalOpen && (
                <BarcodeModal
                    requests={selectedRequest}
                    closeModal={closeModal}
                />
            )}
        </>
    )
}