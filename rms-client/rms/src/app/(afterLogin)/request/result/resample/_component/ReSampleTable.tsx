"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/result/resample/_component/reSampleTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import type {Request} from "@/model/Request";
import {fetchFinishedOrder} from "@/app/(afterLogin)/request/result/download/_api/fetchFinishedOrder";
import {Paging} from "@/model/Paging";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {format} from "date-fns";
import InputBox from "@/app/_component/InputBox";
import GreenButton from "@/app/_component/GreenButton";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

export default function ReSampleTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData && requestData.length > 0 ? requestData.every((row) => row.isSelected) : false;
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"status", asc: true, size:10, page:1});
    const [totalPage, setTotalPage] = useState<number>(0);

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

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };

    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        setSearch((prevSearch) => {
            const updatedFilters = prevSearch.filters?.slice() || [];
            const existingFilterIndex = updatedFilters.findIndex((filter) => filter.key === newFilter.key)
            if (existingFilterIndex !== -1) updatedFilters[existingFilterIndex] = newFilter;
            else updatedFilters.push(newFilter);
            return { ...prevSearch, filters: updatedFilters, page:1 }
        });
    };

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
    };

    const handleRequestOnClick = () => {
        alert("request");
    }
    const handleClosedOnClick = () => {
        alert("closed");
    }

    const fetchData = async (search: Paging) => {
        try {
            const response = await fetchFinishedOrder(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            const data = responseData.data;
            setRequestData(data as Request[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setRequestData([]);
            setTotalPage(0);
        }
    }

    const handleResampleOnClick = () => {
        alert('Resample Click');
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <DatePickerRangeBox
                        label={"date-from-to"}
                        onChange={(from, to) => {
                            handleSearchChange({key: "date_from", value: format(from, "yyyy-MM-dd")})
                            handleSearchChange({key: "date_to", value: format(to, "yyyy-MM-dd")})
                        }}/>
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        handleSearchChange({key: "search", value: value})
                    }}></InputBox>
                </div>
            </section>
            <section>
                <div className={style.reSampleButton}>
                    <GreenButton name={"Re-Sample"} onClick={handleResampleOnClick}/>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                    <th>
                            <label form="agree" className={style.checkbox}>
                                <input
                                    type="checkbox"
                                    checked={isSelectedAll}
                                    onChange={() => handleSelectAll(!isSelectedAll)}
                                    className={style.checkbox}
                                />
                                <span className={style.checkmark}></span>
                            </label>
                        </th>
                        <th>Registration Number</th>
                        <th>Patient(s) Name</th>
                        <th>MRN</th>
                        <th>Resample Notice<br/>(YYYY/MM/DD)</th>
                        <th>Reason</th>
                        <th>Resample</th>
                        <th>Closed</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 && requestData.map((row, rowIndex) => (
                        <tr key={row.order_id! + row.service!.id + row.sample!.id}>
                            <td>
                                <label form="agree" className={style.checkbox}>
                                    <input
                                        type="checkbox"
                                        checked={row.isSelected || false}
                                        onChange={() => handleSelectChange(rowIndex, !row.isSelected)}
                                        className={style.checkbox}
                                    />
                                    <span className={style.checkmark}></span>
                                </label>
                            </td>
                            <td>{row.sample?.barcode}</td>
                            <td>{row.sample?.patient?.name}</td>
                            <td>{row.sample?.patient?.serial}</td>
                            <td>{row.resample_at ? new Date(row.resample_at).toLocaleDateString() : 'N/A'}</td>
                            <td>{row.sample?.resample_reason}</td>
                            <td className={style.request} onClick={handleRequestOnClick}>Request</td>
                            <td className={style.closed} onClick={handleClosedOnClick}>Closed</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <div className={style.pagination}>
                    <span>items per page:</span>
                    <div className={style.select}>
                        <select onChange={handlePageSizeChange}>
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="50">50</option>
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
            </section>
        </>
    );
}