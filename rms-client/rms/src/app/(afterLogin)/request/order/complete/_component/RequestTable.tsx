"use client"

import globalTableStyle from "@/css/globalTable.module.css";
import requestStyle from '@/css/order/requestTable.module.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
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
import RequestInfo from "@/app/(afterLogin)/request/order/_component/RequestInfo";

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
const defaultSearch: Query = {size:10, page:1}
const defaultFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.COMPLETED_ORDER.valueOf()
}

export default function RequestTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [searchFilter, setSearchFilter] = useState<Filter | undefined>(undefined);
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>()
    const [infoModalOpen, setInfoModalOpen] = useState<boolean>(false);
    const [infoRequest, setInfoRequest] = useState<Request>();

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            size: newSize,
            page: 1
        }));
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data = await response.json();
            setRequestData(data as Request[]);
            setTotalPage(totalPage);
        }
        catch {
            setRequestData([]);
        }
    };

    const handleInfoClick = (request: RequestWithSelected) => {
        setInfoRequest(request);
        setInfoModalOpen(true);
    };

    const closeModal = () => {
        setInfoModalOpen(false);
    }

    useEffect(() => {
        const updatedSearch = {
            ...search,
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
    }, [search,searchFilter,orderDateFilter]);

    return (
        <div className={globalTableStyle.container}>
            <div className={globalTableStyle.formGroupBetween}>
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
                        }}
                    />
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
                    <InputBox label={"search"} onChange={(value) => {
                        setSearchFilter(
                            value && value.trim() !== ""
                                ? {
                                    table: selectedOption.table,
                                    column: selectedOption.column,
                                    operator: "LIKE",
                                    value: value
                                } as Filter
                                : undefined
                        );
                    }}></InputBox>
                </div>
            </div>
            <div className={globalTableStyle.tableContainer}>
                <table className={requestStyle.table}>
                    <thead>
                    <tr>
                        <th className={requestStyle.longColumn}>Order Date<br/>(DD-MM-YYYY)</th>
                        <th>Global courier</th>
                        <th className={requestStyle.middleColumn}>AirWaybill no.</th>
                        <th>User Name</th>
                        <th>Institution</th>
                        <th>Registration ID</th>
                        <th>Service</th>
                        <th>Patient(s) Name</th>
                        <th className={requestStyle.longColumn}>Patient(s) DOB<br/>(DD-MM-YYYY)</th>
                        <th>MRN</th>
                        <th>Info</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 ? ( requestData.map((request) => (
                        <tr key={request.order_id! + request.service!.id + request.sample!.id}>
                            <td>{request.create_at ? new Date(request.create_at).toLocaleDateString('en-GB').replace(/\//g, '-') : ''}</td>
                            <td>{request.courier_company}</td>
                            <td>{request.awb_number}</td>
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
                                    className={globalTableStyle.info}
                                    onClick={(e) => {
                                        handleInfoClick(request)
                                        e.stopPropagation();
                                    }}/>
                            </td>
                        </tr>
                    ))
                    ) : (
                        <tr>
                            <td colSpan={10} className={globalTableStyle.noData}>
                                The searched data does not exist
                            </td>
                        </tr>
                    )}
                    </tbody>
                    {infoModalOpen && (
                        <RequestInfo
                            serviceId={infoRequest?.service!.id!}
                            sampleId={infoRequest?.sample!.id!}
                            closeModal={closeModal}
                        />
                    )}
                </table>
            </div>
            <div className={globalTableStyle.pagination}>
                <span>items per page:</span>
                <div className={globalTableStyle.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                    </select>
                </div>
                <span> 1-{totalPage} of {search.page} </span>
                <button
                    className={globalTableStyle.pageButton}
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    className={globalTableStyle.pageButton}
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}