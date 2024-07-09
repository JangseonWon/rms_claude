"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/dashboard/_component/table.module.css"
import type {Request} from "@/model/Request";
import {getRequests} from "@/app/(afterLogin)/request/dashboard/_api/getRequests";
import {format} from "date-fns";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {useSetStatus, useStatus} from "@/app/(afterLogin)/request/dashboard/store/useStatusStore";
import {Paging} from "@/model/Paging";
import DownloadExcelButton from "@/app/(afterLogin)/request/dashboard/_component/DownloadExcelButton";
import {Status} from "@/model/Status";

export default function Table() {
    const [requestData, setRequestData] = useState<Request[]>([]);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"status", asc: true, size:5, page:1});
    const [totalPage, setTotalPage] = useState<number>();
    const status = useStatus();
    const setStatus = useSetStatus();
    const statusList = [
        {name: "ALL", value: Status.TOTAL},
        {name: "ORDERED", value: Status.ORDERED},
        {name: "INPROGRESS", value: Status.INPROGRESS},
        {name: "TESTFAILED", value: Status.TESTFAILED},
        {name: "DELIVERED", value: Status.DELIVERED},
        {name: "COMPLETE", value: Status.COMPLETE}
    ]

    const fetchData = async (search: Paging) => {
        const response = await getRequests(search)
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;
        setRequestData(data as Request[]);
        setTotalPage(totalPage)
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    useEffect(() => {
        console.log('status changed:', status);
        handleSearchChange({key: "status", value: status})
    }, [status]);

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
            size: newSize
        }));
    };
    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        setSearch((prevSearch) => {
            const updatedFilters = prevSearch.filters?.slice() || [];
            const existingFilterIndex = updatedFilters.findIndex((filter) => filter.key === newFilter.key);
            if (existingFilterIndex !== -1) {
                updatedFilters[existingFilterIndex] = newFilter;
            } else {
                updatedFilters.push(newFilter);
            }
            return { ...prevSearch, filters: updatedFilters, page:1 }
        });
    };

    const handleSelectStatusChange = (status: Status) => {
        setStatus(status);
    }

    const formatDate = (year: number | undefined, month: number | undefined, day: number | undefined) => {
        const parts = [];

        if (year !== undefined) {
            parts.push(year.toString());
        }
        if (month !== undefined) {
            parts.push(format(new Date(year ?? 0, month - 1, 1), "MMM"));
        }
        if (day !== undefined) {
            parts.push(day.toString());
        }

        return parts.length > 0 ? parts.join('-') : '-';
    };

    return (
        <div className={style.container}>
            <div className={style.filterContainer}>
                <div className={style.filterContainerRight}>
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) =>{
                            handleSearchChange({ key: "date_from", value: format(from, "yyyy-MM-dd")})
                            handleSearchChange({ key: "date_to", value: format(to, "yyyy-MM-dd")})
                        }}/>
                    <SelectBox value={status} options={statusList} label={"status"} onChange={(selectedOption) =>{
                        handleSelectStatusChange(selectedOption.value);
                    }}/>
                </div>
                <div className={style.filterContainerLeft}>
                    <DownloadExcelButton requestData={requestData} status={status} />
                    <div className={style.search}>
                        <InputBox label={"search"} onChange={(value) =>{
                            handleSearchChange({key: "search", value: value})
                        }}></InputBox>
                    </div>
                </div>
            </div>
            <table className={style.table}>
                <thead>
                <tr>
                    <th>Registration ID</th>
                    <th>User Name</th>
                    <th>Institution</th>
                    <th>Service</th>
                    <th>Patient(s) Name</th>
                    <th>MRN</th>
                    <th>Patient BOD</th>
                    <th>Current Status</th>
                    <th>Order Date</th>
                </tr>
                </thead>
                <tbody>
                {requestData && requestData.length > 0 && requestData.map((row) => (
                    <tr key={`${row.order_id}${row.sample?.id}${row.service!.id}`}>
                        <td>{row.sample!.barcode}</td>
                        <td>{row.sample!.patient!.organization!.user!.id}</td>
                        <td>{row.sample!.patient!.organization!.id}</td>
                        <td>{row.service!.name}</td>
                        <td>{row.sample!.patient!.name}</td>
                        <td>{row.sample!.patient!.serial}</td>
                        <td>{row.sample?.patient ?
                            formatDate(row.sample.patient.birth_year, row.sample.patient.birth_month, row.sample.patient.birth_day) : '-'}
                        </td>
                        <td>{row.status}</td>
                        <td>{row.create_at ? format(new Date(row.create_at), "yyyy-MMM-dd") : '-'}</td>
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
                    disabled={search.page === 1}
                    onClick={() => handlePageChange(search.page - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange(search.page + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}