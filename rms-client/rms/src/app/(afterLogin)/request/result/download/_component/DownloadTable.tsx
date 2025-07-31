"use client"

import React, {useEffect, useState} from "react";
import downloadStyle from "@/app/(afterLogin)/request/result/download/_component/downloadTable.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import {faAngleLeft, faAngleRight, faFilePdf} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import type {Request} from "@/model/Request";
import {postRequests} from "@/app/(afterLogin)/request/result/download/_api/postRequests";
import {getReportFile} from "@/app/(afterLogin)/request/result/download/_api/getReportFile";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Filter} from "@/model/Filter";
import {Query} from "@/model/Query";
import {Report} from "@/model/Report";
import {getReportFiles} from "@/app/(afterLogin)/request/result/download/_api/getReportFiles";
import {Status} from "@/model/Status";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import CellTooltip from "@/app/_component/CellToolTip";
import style from "@/css/qna/qnaTable.module.css";
import {GrPowerReset} from "react-icons/gr";
import {formatDateLocal} from "@/app/_component/DateUtil";
import {format} from "date-fns";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" },
    { table: "request", column: "status", name: "Status" }
];
const deliveredFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.DELIVERED.valueOf()
}
const completedFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.COMPLETED.valueOf()
}
const notCancelFilter: Filter = {
    table: "request",
    column: "is_cancel",
    value: "false",
    operator: "="
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
            condition_type: "OR",
            filters: [
                deliveredFilter,
                completedFilter
            ]
        },
        {
            condition_type: "OR",
            filters: [
                notCancelFilter
            ]
        }
    ],
    size:10,
    page:1
}

export default function DownloadTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData && requestData.length > 0 ? requestData.every((row) => row.isSelected) : false;
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [fromDate, setFromDate] = useState<Date | null>(null);
    const [toDate, setToDate] = useState<Date | null>(null);
    const showAlert = CallAlertDialog();

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

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
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
                        condition_type: "OR",
                        filters: [
                            deliveredFilter,
                            completedFilter
                        ]
                    },
                    {
                        filters: [
                            newFilter,
                            notCancelFilter
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
        setFromDate(from);
        setToDate(to);
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
            const response = await postRequests(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setRequestData(responseData as Request[]);
            setTotalPage(totalPage)
        } catch(error) {
            setRequestData([]);
            setTotalPage(0);
        }
    }

    useEffect(() => {
        fetchData(search);
    }, [search]);

    const handleDownloadOnClick = async (report: Report, request: Request) => {
        try {
            const response = await getReportFile(report.id!);
            if (response.ok) {
                const blob = await response.blob();
                const url = URL.createObjectURL(blob)
                const a = document.createElement('a');
                a.href = url;
                a.download = `${request.sample?.barcode || 'NA'}_${request.service?.id || 'NA'}.pdf`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                URL.revokeObjectURL(url);
            } else {
                showAlert("Download failed")
            }
        } catch (error) {
            showAlert("error");
        }
    };

    const handleBatchDownloadClick = async () => {
        const selectedRequest: Request[] = requestData.filter(row => row.isSelected) as Request[]
        try {
            const response = await getReportFiles(selectedRequest);
            if (response.ok) {
                const blob = await response.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                const today = new Date().toISOString().split('T')[0];
                a.href = url;
                a.download = `GCGenome_reports_${today}.zip`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            } else {
                showAlert("Download failed")
            }
        } catch (error) {
            console.error("Failed to fetch multi download file:", error);
        }
    }

    const handleReset = () => {
        setFromDate(null)
        setToDate(null)
        setSearch((prevSearch) => {
            const updatedFilterGroups = (prevSearch.filter_groups || []).filter(group =>
                !group.filters?.some(filter => filter.column === "create_at")
            );

            return {
                ...prevSearch,
                filter_groups: updatedFilterGroups,
                page: 1
            };
        });
    };

    return (
        <div className={globalTableStyle.container}>
            <div className={globalTableStyle.formGroupRight}>
                <BlueButton name={"Bulk Download"} onClick={handleBatchDownloadClick}/>
            </div>
            <div className={globalTableStyle.formGroupBetween}>
                <div>
                    <DatePickerRangeBox
                        label={"from-to"}
                        fromDate={fromDate}
                        toDate={toDate}
                        onChange={(from, to) => {
                            addDateFilter(from, to);
                        }}
                    />
                    <GrPowerReset
                        className={style.resetButton}
                        onClick={handleReset}
                    />
                </div>
                <div className={downloadStyle.filterContainerLeft}>
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
                        handleSearchChange(selectedOption, value)
                    }}></InputBox>
                </div>
            </div>
            <section className={globalTableStyle.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th className={globalTableStyle.stickyColumnHeaderCheckBox}>
                            <label form="agree" className={globalTableStyle.checkbox}>
                                <input
                                    type="checkbox"
                                    checked={isSelectedAll}
                                    onChange={() => handleSelectAll(!isSelectedAll)}
                                    className={globalTableStyle.checkbox}
                                />
                                <span className={globalTableStyle.checkmark}></span>
                            </label>
                        </th>
                        <th className={globalTableStyle.middleColumn}>Order Date<br/>(YYYY-MM-DD)</th>
                        <th className={globalTableStyle.middleColumn}>Resample</th>
                        <th className={globalTableStyle.longColumn}>User Name</th>
                        <th className={globalTableStyle.middleColumn}>Institution</th>
                        <th className={globalTableStyle.longColumn}>Registration ID</th>
                        <th className={globalTableStyle.longColumn}>Service</th>
                        <th className={globalTableStyle.longColumn}>Patient(s) Name</th>
                        <th className={globalTableStyle.longColumn}>MRN</th>
                        <th className={globalTableStyle.middleColumn}>Report Date<br/>(YYYY-MM-DD)</th>
                        <th className={globalTableStyle.middleColumn}>Status</th>
                        <th className={globalTableStyle.middleColumn}>Report Download</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 ? ( requestData.map((request, rowIndex) => {
                            const latestPdfReport = (request.reports as Report[])?.filter(
                                (report) => report.type === 'PDF' && report.is_latest === true
                            )[0];
                            return (
                                <tr key={`${request.service!.id}${request.sample!.id}`}>
                                <td className={globalTableStyle.stickyColumnCheckBox}>
                                    <label form="agree" className={globalTableStyle.checkbox}>
                                        <input
                                            type="checkbox"
                                            checked={request.isSelected || false}
                                            onChange={() => handleSelectChange(rowIndex, !request.isSelected)}
                                            className={globalTableStyle.checkbox}
                                        />
                                        <span className={globalTableStyle.checkmark}></span>
                                    </label>
                                </td>
                                <td className={globalTableStyle.middleColumn}>{request.create_at ? formatDateLocal(new Date(request.create_at)) : ''}</td>
                                <td className={globalTableStyle.middleColumn}>{request.request_relation?.id == 2 && request.request_relation.name}</td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.user?.name}/></td>
                                <td className={globalTableStyle.middleColumn}><CellTooltip text={request.sample?.patient?.organization?.name}/></td>
                                <td className={globalTableStyle.longColumn}>{request.sample?.barcode}</td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.service?.name}/></td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.sample?.patient?.name}/></td>
                                <td className={globalTableStyle.longColumn}>{request.sample?.patient?.serial}</td>
                                <td className={globalTableStyle.middleColumn}>{latestPdfReport?.create_at ? formatDateLocal(new Date(latestPdfReport.create_at)) : ''}</td>
                                <td className={globalTableStyle.middleColumn}>{request.status}</td>
                                <td className={globalTableStyle.middleColumn}>
                                    {latestPdfReport ? (
                                        <FontAwesomeIcon
                                            key={latestPdfReport.id}
                                            className={downloadStyle.downloadIcon}
                                            icon={faFilePdf}
                                            onClick={() => handleDownloadOnClick(latestPdfReport!, request)}
                                        />
                                    ) : null}
                                </td>
                            </tr>
                            )
                    })
                    ) : (
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
    );
}