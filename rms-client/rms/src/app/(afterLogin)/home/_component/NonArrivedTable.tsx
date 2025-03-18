"use client"

import style from "@/app/(afterLogin)/home/_component/nonArrivedTable.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import React, {useEffect, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FilterGroup, Query} from "@/model/Query";
import {postRequests} from "@/app/(afterLogin)/home/_api/postRequests";
import type {Request} from "@/model/Request";
import {Filter} from "@/model/Filter";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Status} from "@/model/Status";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {formatDateLocal, getStringDateFromComponents} from "@/app/_component/DateUtil";


const selectBoxOptions: SelectBoxOption[] = [
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "serial", name: "MRN" },
    { table: "request", column: "courier_company", name: "Global courier" },
    { table: "request", column: "awb_number", name: "Airwaybill" },
];

const getTenDaysAgo = () => {
    const today = new Date();
    today.setDate(today.getDate() - 10);
    return today.toISOString().split('T')[0]; // "YYYY-MM-DD" 형식으로 반환
};

const defaultFilterGroup: FilterGroup = {
    filters : [
            {
                table: "request",
                column: "status",
                operator: "=",
                value: Status.COMPLETED_ORDER
            },
            {
                table: "request",
                column: "confirmed_at",
                operator: "<=",
                value: getTenDaysAgo()
            }
]
}

export default function NonArrivedTable() {
    const [requestData, setRequestData] = useState<Request[]>([]);
    const [search, setSearch] = useState<Query>({size:10, page:1, filter_groups:[defaultFilterGroup]});
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [filter, setFilter] = useState<Filter>({
        table: selectBoxOptions[0].table!,
        column: selectBoxOptions[0].column!,
        operator: "LIKE",
        value: ""
    })
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>()

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setRequestData(responseData as Request[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setRequestData([]);
            setTotalPage(0);
        }
    }

    useEffect(() => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups: [
                defaultFilterGroup,
                ...(orderDateFilter ? [orderDateFilter] : []),
                {
                    filters: [filter]
                }
            ]
        }));
    }, [filter, orderDateFilter]);

    useEffect(() => {
        fetchData(search)
    }, [search]);

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

    return (
        <div>
            <div className={style.line}></div>
            <h1>Non-arrived (+10days)</h1>
            <div className={globalTableStyle.formGroupBetween}>
                <div className={globalTableStyle.formGroupLeft}>
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) => {
                            setOrderDateFilter(
                                from && to ? {
                                    filters: [
                                        {
                                            table: "request",
                                            column: "create_at",
                                            value: formatDateLocal(from) ,
                                            operator: ">="
                                        },
                                        {
                                            table: "request",
                                            column: "create_at",
                                            value: formatDateLocal(to) ,
                                            operator: "<="
                                        }
                                    ]
                                } as FilterGroup: undefined
                            )
                        }}/>
                </div>
                <div className={globalTableStyle.formGroupRight}>
                    <SelectBox
                        width={'155px'}
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={"Filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                            setFilter(prev => ({
                                ...prev,
                                table: option.table!,
                                column: option.column!
                            }))
                        }}
                    />
                    <div className={style.search}>
                        <InputBox label={"Search"} onChange={(value) => {
                            setFilter(prev => ({
                                ...prev,
                                value: value
                            }))
                        }}></InputBox>
                    </div>
                </div>
            </div>
            <section className={style.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th>Order Date<br/>(YYYY-MM-DD)</th>
                        <th>Confirmed Date<br/>(YYYY-MM-DD)</th>
                        <th>Registration ID</th>
                        <th>User Name</th>
                        <th>Institution</th>
                        <th>Patient(s) Name</th>
                        <th>Service</th>
                        <th>MRN</th>
                        <th>Patient BOD<br/>(YYYY-MM-DD)</th>
                        <th>Global<br/>courier</th>
                        <th>Airwaybill</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 ? ( requestData.map((request, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{request.create_at ? formatDateLocal(new Date(request.create_at))  : ''}</td>
                            <td>{request.confirmed_at ? formatDateLocal(new Date(request.confirmed_at))  : ''}</td>
                            <td>{request.sample?.barcode}</td>
                            <td>{request.user?.name}</td>
                            <td>{request.sample?.patient?.organization?.name}</td>
                            <td>{request.sample?.patient?.name}</td>
                            <td>{request.service?.name}</td>
                            <td>{request.sample?.patient?.serial}</td>
                            <td>{getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}</td>
                            <td>{request.courier_company}</td>
                            <td>{request.awb_number}</td>
                        </tr>
                    ))) : (
                        <tr>
                            <td colSpan={11} className={globalTableStyle.noData}>
                    The searched data does not exist
                    </td>
                </tr>
                )}
                    </tbody>
                </table>
            </section>
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
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}