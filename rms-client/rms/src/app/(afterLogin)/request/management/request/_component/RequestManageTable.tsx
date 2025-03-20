"use client"

import React, {useEffect, useState} from "react";
import globalTableStyle from "@/css/globalTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Query} from "@/model/Query";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Request} from "@/model/Request";
import {postRequests} from "@/app/(afterLogin)/request/management/request/_api/postRequests";
import {formatDateLocal, getStringDateFromComponents} from "@/app/_component/DateUtil";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {format} from "date-fns";
import {Filter} from "@/model/Filter";
import {Status} from "@/model/Status";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "user", column: "name", name: "거래처명" },
    { table: "organization", column: "name", name: "의뢰기관" },
    { table: "sample", column: "barcode", name: "의뢰번호" },
    { table: "patient", column: "name", name: "환자명" },
    { table: "patient", column: "sex", name: "성별" },
    { table: "request", column: "physician", name: "담당의" },
    { table: "service", column: "name", name: "의뢰명" },
    { table: "patient", column: "serial", name: "MRN" },
    { table: "service", column: "id", name: "의뢰코드" },
    { table: "request", column: "courier_company", name: "배송업체" },
    { table: "request", column: "awb_number", name: "운송번호" },
];

const defaultFilter: Filter = {
    table: "request",
    column: "status",
    value: Status.CART.valueOf(),
    operator: "!="
}

const defaultSearch: Query = {
    sorts: [
        {
            table: "sample",
            column: "barcode",
            asc: false
        }
    ],
    filter_groups: [
        {
            filters: [
                defaultFilter
            ]
        }
    ],
    size:10,
    page:1
}

export default function RequestManageTable() {
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [requests, setRequests] = useState<RequestWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);

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
    const handleSearchChange = (option: SelectBoxOption, value: string) => {
        setSearch((prevSearch) => {
            const newFilter = {
                table: option.table!,
                column: option.column!,
                value: value,
                operator: "LIKE"
            };

            return {
                ...prevSearch,
                filter_groups: [
                    {
                        condition_type: "AND",
                        filters: [
                            newFilter,
                            defaultFilter
                        ]
                    },
                    ...(prevSearch.filter_groups || []).filter(group => group.filters?.some(filter => filter.column === "create_at"))
                ],
                page: 1
            };
        });
    };
    const addDateFilter = (from: Date | null, to: Date | null) => {
        if (!from || !to) return;

        setSearch((prevSearch) => {
            const updatedFilters = (prevSearch.filter_groups || []).filter(group =>
                !group.filters?.some(filter => filter.column === "create_at")
            ) || [];

            return {
                ...prevSearch,
                filter_groups: [
                    ...updatedFilters,
                    {
                        condition_type: "AND",
                        filters: [
                            {
                                table: "request",
                                column: "create_at",
                                value: format(from, "yyyy-MM-dd"),
                                operator: ">="
                            },
                            {
                                table: "request",
                                column: "create_at",
                                value: format(to, "yyyy-MM-dd"),
                                operator: "<="
                            }
                        ]
                    }
                ],
                page: 1
            };
        });
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data = await response.json();
            setRequests(data as Request[]);
            setTotalPage(totalPage);
        }
        catch {
            setRequests([]);
        }
    };

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <div>
            <div className={globalTableStyle.container}>
                <div className={globalTableStyle.formGroupBetween}>
                    <div>
                        <DatePickerRangeBox
                            label={"from-to"}
                            onChange={(from, to) => {
                                addDateFilter(from, to);
                            }}
                        />
                    </div>
                    <div>
                        <SelectBox
                            width={"140px"}
                            value={selectedOption.name}
                            options={selectBoxOptions}
                            label={"status"}
                            onChange={(selectedOption) => {
                                setSelectedOption(selectedOption);
                            }}
                        />
                        <InputBox label={"search"} onChange={(value) => {
                            handleSearchChange(selectedOption, value)
                        }}/>
                    </div>
                </div>
            </div>
            <div className={globalTableStyle.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th>의뢰날짜<br/>(YYYY-MM-DD)</th>
                        <th>거래처명</th>
                        <th>의뢰기관</th>
                        <th>의뢰번호</th>
                        <th>환자명</th>
                        <th>생년월일<br/>(YYYY-MM-DD)</th>
                        <th>성별</th>
                        <th>담담의</th>
                        <th>검체채취일<br/>(YYYY-MM-DD)</th>
                        <th>의뢰명</th>
                        <th>MRN</th>
                        <th>의뢰코드</th>
                        <th>배송업체</th>
                        <th>운송번호</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requests && requests.length > 0 && requests.map((request, rowIndex) => (
                        <tr key={rowIndex}>
                            <td className={globalTableStyle.middleColumn}>{request.create_at ? formatDateLocal(new Date(request.create_at)) : ''}</td>
                            <td className={globalTableStyle.longColumn}>{request.user?.name}</td>
                            <td className={globalTableStyle.longColumn}>{request.sample?.patient?.organization?.name}</td>
                            <td className={globalTableStyle.longColumn}>{request.sample?.barcode}</td>
                            <td className={globalTableStyle.longColumn}>{request.sample?.patient?.name}</td>
                            <td className={globalTableStyle.middleColumn}>{getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}</td>
                            <td className={globalTableStyle.shortColumn}>{request.sample?.patient?.sex}</td>
                            <td className={globalTableStyle.shortColumn}>{request.physician}</td>
                            <td className={globalTableStyle.middleColumn}>{request.sample?.sampling_on ? formatDateLocal(new Date(request.sample.sampling_on)) : '-'}</td>
                            <td className={globalTableStyle.longColumn}>{request.service?.name}</td>
                            <td className={globalTableStyle.shortColumn}>{request.sample?.patient?.serial}</td>
                            <td className={globalTableStyle.shortColumn}>{request.service?.id}</td>
                            <td className={globalTableStyle.shortColumn}>{request.courier_company}</td>
                            <td className={globalTableStyle.shortColumn}>{request.awb_number}</td>
                        </tr>
                    ))}
                    </tbody>
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
    );
}