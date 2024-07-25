"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/result/download/_component/downloadTable.module.css";
import {faAngleLeft, faAngleRight, faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import type {Request} from "@/model/Request";
import {fetchFinishedOrder} from "@/app/(afterLogin)/request/result/download/_api/fetchFinishedOrder";
import {fetchDownloadFile} from "@/app/(afterLogin)/request/result/download/_api/fetchDownloadFile";
import {useOpenAlertDialog, useSetIconAlertDialog, useSetMessageAlertDialog} from "@/store/useAlertDialogStore";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {format} from "date-fns";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {fetchMultiDownloadFile} from "@/app/(afterLogin)/request/result/download/_api/fetchMultiDownloadFile";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

export default function DownloadTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData && requestData.length > 0 ? requestData.every((row) => row.isSelected) : false;
    const [totalPage, setTotalPage] = useState<number>(0);
    const [status, setStatus] = useState<string>('-');
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"status", asc: true, size:5, page:1});
    const statusList = [
        {name:"DELIVERED", value:"DELIVERED"},
        {name:"COMPLETE", value:"FINISHED"}
    ];

    const setShowAlertDialog = useOpenAlertDialog();
    const setMessage = useSetMessageAlertDialog();
    const setIcon = useSetIconAlertDialog();

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

    useEffect(() => {
        fetchData(search)
    }, [search]);

    const handleDownloadOnClick = async (requestId: string) => {
        try {
            const response = await fetchDownloadFile(requestId);
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${requestId}.pdf`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                fetchData(search);
            } else {
                setMessage(response.statusText);
                setShowAlertDialog(true);
                setIcon('error');
            }
        } catch (error) {
            console.error("Failed to fetch download file:", error);
        }
    };

    const handleMultiDownloadOnClick = async () => {
        const selectedIds = requestData.filter(row => row.isSelected).map(row => `${row.sample?.barcode}_${row.service?.id}`);
        if (selectedIds.length === 0) {
            setMessage("No items selected for download.");
            setShowAlertDialog(true);
            setIcon('warning');
            return;
        }

        try {
            const response = await fetchMultiDownloadFile(selectedIds);
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                const today = new Date().toISOString().split('T')[0];
                a.href = url;
                a.download = `reports_${today}.zip`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                fetchData(search);
            } else {
                setMessage(response.statusText);
                setShowAlertDialog(true);
                setIcon('error');
            }
        } catch (error) {
            console.error("Failed to fetch multi download file:", error);
        }
    }

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
                    <SelectBox options={statusList} label={"status"} value={status} onChange={(value) => {
                        setStatus(value.value);
                        handleSearchChange({key: "status", value: value.value})
                    }}/>
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        handleSearchChange({key: "search", value: value})
                    }}></InputBox>
                </div>
            </section>
            <section>
                <button className={style.downloadButton} onClick={handleMultiDownloadOnClick}>
                    Download
                </button>
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
                        <th>Institution</th>
                        <th>Physician Name</th>
                        <th>Report out<br/>(YYYY/MM/DD)</th>
                        <th>Status</th>
                        <th>Report Download</th>
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
                            <td>{row.sample?.patient?.organization?.id}</td>
                            <td>{row.physician}</td>
                            <td>{row.complete_at ? new Date(row.complete_at).toLocaleDateString() : 'N/A'}</td>
                            <td>{row.status}</td>
                            <td>
                                <FontAwesomeIcon
                                    className={style.downloadIcon}
                                    icon={faDownload}
                                    onClick={() => handleDownloadOnClick(`${row.sample?.barcode}_${row.service?.id}` || '')}/>
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