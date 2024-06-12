"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/dashboard/dashboard/_component/table.module.css"
import type {Request} from "@/model/Request";
import {getRequests} from "@/app/(afterLogin)/request/dashboard/dashboard/_api/getRequests";
import {format} from "date-fns";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {Search} from "@/model/Search";

export default function Table() {
    const [requestData, setRequestData] = useState<Request[]>([])
    const [search, setSearch] = useState<Search>({page:{size:5, number:1}})
    const [totalPage, setTotalPage] = useState<number>()
    const statusList = [
        {name:"ORDERED", value:"ORDERED"},
        {name:"INPROGRESS", value:"INPROGRESS"},
        {name:"TESTFAILED", value:"TESTFAILED"},
        {name:"DELIVERED", value:"DELIVERED"},
        {name:"COMPLETE", value:"FINISHED"}
    ]

    const fetchData = async (search: Search) => {
        const response = await getRequests(search)
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const data = await response.json();
        setRequestData(data as Request[]);
        setTotalPage(totalPage)
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({...prevPage, page:{ ...prevPage.page, number: newPageNumber}}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            page: {
                ...prevSearch.page,
                size: newSize
            }
        }));
    };
    const handleSearchChange = (newFilter: { field: string; value: string }) => {
        setSearch((prevSearch) => {
            const updatedFilters = prevSearch.filters?.slice() || [];
            const existingFilterIndex = updatedFilters.findIndex((filter) => filter.field === newFilter.field)
            if (existingFilterIndex !== -1) updatedFilters[existingFilterIndex] = newFilter;
            else updatedFilters.push(newFilter);
            return { ...prevSearch, filters: updatedFilters }
        });
    };

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
                            handleSearchChange({ field: "date_from", value: format(from, "yyyy-MM-dd")})
                            handleSearchChange({ field: "date_to", value: format(to, "yyyy-MM-dd")})
                        }}/>
                    <SelectBox options={statusList} label={"status"} onChange={(value) =>{
                        handleSearchChange({field: "status", value: value.value})
                    }}/>
                </div>
                <div className={style.filterContainerLeft}>
                    <InputBox label={"search"} onChange={(value) =>{
                        handleSearchChange({field: "search", value: value})
                    }}></InputBox>
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
                {requestData.map((row) => (
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
                <span> 1-{totalPage} of {search.page.number} </span>
                <button
                    disabled={search.page.number === 1}
                    onClick={() => handlePageChange(search.page.number - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={search.page.number === totalPage}
                    onClick={() => handlePageChange(search.page.number + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}