"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/dashboard/_component/table.module.css"
import type {Request} from "@/model/Request";
import {postRequests} from "@/app/(afterLogin)/request/dashboard/_api/postRequests";
import {format} from "date-fns";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {useSetStatus, useStatus} from "@/app/(afterLogin)/request/dashboard/store/useStatusStore";
import {Query} from "@/model/Query";
import DownloadExcelButton from "@/app/(afterLogin)/request/dashboard/_component/DownloadExcelButton";
import {Status} from "@/model/Status";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {GrPowerReset} from "react-icons/gr";

export default function Table() {
    const [requestData, setRequestData] = useState<Request[]>([]);
    const globalStatus = useStatus();
    const [search, setSearch] = useState<Query>(
        {
            sort_by:"create_at",
            asc: false,
            size:5,
            page:1,
            filter_groups: [
                    {
                        condition_type: "AND",
                        filters: [{
                                    table: 'request',
                                    column: 'status',
                                    value: globalStatus,
                                    operator: "="
                                }
                        ]
                    }
                ]
        });
    const [searchValue, setSearchValue] = useState<string>('');
    const [totalPage, setTotalPage] = useState<number>();
    const [pageRange, setPageRange] = useState<{ start: number, end: number }>({ start: 1, end: 5 });
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "sample", column: "barcode", name: "Registration ID" });
    const status = useStatus();
    const setStatus = useSetStatus();
    const selectBoxOptions: SelectBoxOption[] = [
        { table: "sample", column: "barcode", name: "Registration ID" },
        { table: "organization", column: "id", name: "Institution" },
        { table: "patient", column: "name", name: "Patient(s) Name" },
        { table: "service", column: "name", name: "Service" },
        { table: "patient", column: "serial", name: "MRN" },
        { table: "patient", column: "birth_year", name: "Patient BOD" },
        { table: "request", column: "report_at", name: "Report Date" },
        { table: "user", column: "name", name: "User Name" },
    ];

    const fetchData = useCallback(async (search: Query) => {
        const response = await postRequests(search)
        if (response.ok) {
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setRequestData(responseData as Request[]);
            setTotalPage(totalPage);
        }
    },[]);

    useEffect(() => {
        fetchData(search)
    }, [search]);

    useEffect(() => {
        handleSearchChange(selectOption);
    }, [status, searchValue]);

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage => ({
            ...prevPage,
            page: newPageNumber
        }));
        if (newPageNumber < pageRange.start || newPageNumber > pageRange.end) {
            const newStart = Math.floor((newPageNumber - 1) / 10) * 10 + 1;
            setPageRange({ start: newStart, end: newStart + 9 });
        }
    };

    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            size: newSize,
            page: 1
        }));
    };
    const handleSearchChange = (option: SelectBoxOption) => {
        setSearch((prevSearch) => {
            const newFilter = {
                table: option.table!,
                column: option.column!,
                value: searchValue,
                operator: "LIKE"
            };

            return {
                ...prevSearch,
                filter_groups: [
                    {
                        condition_type: "AND",
                        filters: [
                            newFilter,
                            ...(status !== Status.TOTAL
                                ? [{
                                    table: 'request',
                                    column: 'status',
                                    value: status,
                                    operator: "="
                                }]
                                : [{
                                    table: 'request',
                                    column: 'status',
                                    value: 'CART',
                                    operator: "!="
                                }])
                        ]
                    },
                    ...(prevSearch.filter_groups || []).filter(group => group.filters?.some(filter => filter.column === "create_at"))
                ],
                page: 1
            };
        });
    };

    const handleSearchValueChange = (value: string) => {
        setSearchValue(value);
    };

    const formatDate = (year: number | undefined, month: number | undefined, day: number | undefined) => {
        if (year !== undefined && month !== undefined && day !== undefined) {
            const date = new Date(year, month - 1, day);
            return format(date, "dd-MM-yyyy");
        }
        return "-";
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

    const handleReset = () => {
        setSearch({ sort_by: "create_at", asc: false, size: 5, page: 1 });
        setSearchValue('');
        setStatus(Status.TOTAL);
        setSelectOption({ table: "sample", column: "id", name: "Registration ID" });
    };

    return (
        <div className={style.container}>
            <div style={{float: "right"}}>
                <DownloadExcelButton
                    search={Object.fromEntries(Object.entries(search).filter(([key]) => !['page', 'size'].includes(key)))}
                    status={status}
                />
            </div>
            <div className={style.filterContainer}>
                <div className={style.filterContainerRight}>
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) =>{
                            addDateFilter(from, to);
                        }}/>
                    <GrPowerReset
                        className={style.resetButton}
                        onClick={handleReset}/>
                </div>
                <div className={style.filterContainerLeft}>
                    <SelectBox
                        width={'155px'}
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={" "}
                        onChange={(selectedOption) => {
                            setSelectOption(selectedOption);
                        }}
                    />
                    <div className={style.search}>
                        <InputBox label={""} onChange={(value) =>{
                            handleSearchValueChange(value);
                        }}></InputBox>
                    </div>
                </div>
            </div>
            <table className={style.table}>
                <thead>
                <tr>
                    <th>Order Date<br/>(DD/MM/YYYY)</th>
                    <th>Registration ID</th>
                    <th>User Name</th>
                    <th>Institution</th>
                    <th>Service</th>
                    <th>Patient(s) Name</th>
                    <th>MRN</th>
                    <th>Patient BOD<br/>(DD/MM/YYYY)</th>
                    <th>Current Status</th>
                    <th>Report Date<br/>(DD/MM/YYYY)</th>
                </tr>
                </thead>
                <tbody>
                {requestData && requestData.length > 0 && requestData.map((row) => (
                    <tr key={`${row.order_id}${row.sample?.id}${row.service!.id}`}>
                        <td>{row.create_at ? format(new Date(row.create_at), "dd-MM-yyyy") : '-'}</td>
                        <td>{row.sample!.barcode}</td>
                        <td>{row.sample!.patient!.organization!.user!.name}</td>
                        <td>{row.sample!.patient!.organization!.id}</td>
                        <td>{row.service!.name}</td>
                        <td>{row.sample!.patient!.name}</td>
                        <td>{row.sample!.patient!.serial}</td>
                        <td>{row.sample?.patient ?
                            formatDate(row.sample.patient.birth_year, row.sample.patient.birth_month, row.sample.patient.birth_day) : '-'}
                        </td>
                        <td>{row.status}</td>
                        <td>
                            {row.report?.create_at && !isNaN(new Date(row.report.create_at).getTime())
                                ? format(new Date(row.report.create_at), "dd-MM-yyyy")
                                : '-'}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className={style.pagination}>
                <span>items per page:</span>
                <div className={style.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                    </select>
                </div>
                <span> 1-{totalPage} of {search.page} </span>
                <button
                    className={style.pageButton}
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    className={style.pageButton}
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}