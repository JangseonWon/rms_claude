"use client"

import type {Request} from "@/model/Request";
import React, {useEffect, useState} from "react";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {FilterGroup, Query} from "@/model/Query";
import {Filter} from "@/model/Filter";
import {postRequests} from "@/app/(afterLogin)/request/result/download/_api/postRequests";
import globalTableStyle from "@/css/globalTable.module.css";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import downloadStyle from "@/app/(afterLogin)/request/result/download/_component/downloadTable.module.css";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight, faFilePdf} from "@fortawesome/free-solid-svg-icons";
import {Status} from "@/model/Status";
import {patchRequests} from "@/app/(afterLogin)/request/result/download/_api/patchRequests";
import RequestModal from "@/app/(afterLogin)/request/result/resample/_component/RequestModal";
import CellTooltip from "@/app/_component/CellToolTip";
import style from "@/css/qna/qnaTable.module.css";
import {GrPowerReset} from "react-icons/gr";
import {
    useOkNotice,
    useOpenNoticeDialog,
    useSetMessageNoticeDialog,
    useSetOkNotice
} from "@/store/useNoticeDialogStore";
import {formatDateLocal} from "@/app/_component/DateUtil";
import {Report} from "@/model/Report";
import {getReportFile} from "@/app/(afterLogin)/request/result/download/_api/getReportFile";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

const selectBoxOptions: SelectBoxOption[] = [
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" }
];

const defaultSearch: Query = {size:10, page:1}
const defaultFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.TEST_FAILED.valueOf()
}
const notCancelFilter: Filter = {
    table: "request",
    column: "is_cancel",
    value: "false",
    operator: "="
}


export default function ReSampleTable() {
    const setShowNoticeDialog = useOpenNoticeDialog();
    const setNoticeMessage = useSetMessageNoticeDialog();
    const okNotice = useOkNotice();
    const setOkNotice = useSetOkNotice();
    const [cancelRequest, setCancelRequest] = useState<Request | undefined>(undefined);
    const [requestData, setRequestData] = useState<Request[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [searchFilter, setSearchFilter] = useState<Filter | null>(null);
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>()
    const [requestModalOpen, setRequestModalOpen] = useState<boolean>(false);
    const [selectedRequest, setSelectedRequest] = useState<Request>();
    const showAlert = CallAlertDialog();

    const handleRequestClick = (request: Request) => {
        setSelectedRequest(request);
        setRequestModalOpen(true);
    }
    const closeModal = () => {
        setSelectedRequest(undefined);
        setRequestModalOpen(false);
    }

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

    const handleCancelToResampleClick = (request: Request) => {
        setCancelRequest(request);
        setShowNoticeDialog(true);
        setNoticeMessage('If you cancle the re-sample request, no further test will be performed. Would you still want to cancle?');
    };

    const handleResampleCancel = async (request: Request) => {
        const updateRequest: Request = {
            sample: {id: request.sample?.id},
            service: {id: request.service?.id},
            status: Status.COMPLETED.valueOf()
        }
        const response = await patchRequests([updateRequest])
        if(response.ok){
            const updatedSearch = {
                ...search,
                filter_groups: [
                    ...(orderDateFilter ? [orderDateFilter] : []),
                    {
                        filters: [
                            defaultFilter,
                            notCancelFilter,
                            ...(searchFilter ? [searchFilter] : []),
                        ],
                    }
                ],
            };
            fetchData(updatedSearch);
            setCancelRequest(undefined);
        }
    }
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
                //fetchData(updateSearch);
            } else {
                showAlert("Download failed")
            }
        } catch (error) {
            showAlert("error");
        }
    };

    const refreshData = () => {
        const updatedSearch = {
            ...search,
            filter_groups: [
                ...(orderDateFilter ? [orderDateFilter] : []),
                {
                    filters: [
                        defaultFilter,
                        notCancelFilter,
                        ...(searchFilter ? [searchFilter] : []),
                    ],
                },
            ],
        };
        fetchData(updatedSearch);
    }

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

    const handleReset = () => {
        setSearchFilter(null);
        setSelectedOption(selectBoxOptions[0]);
        setOrderDateFilter(undefined);
    };

    useEffect(() => {
        const updatedSearch = {
            ...search,
            filter_groups: [
                ...(orderDateFilter ? [orderDateFilter] : []),
                {
                    filters: [
                        defaultFilter,
                        notCancelFilter,
                        ...(searchFilter ? [searchFilter] : []),
                    ],
                }
            ],
        };
        fetchData(updatedSearch);
    }, [search,searchFilter,orderDateFilter]);

    useEffect(() => {
        if (cancelRequest && okNotice) {
            handleResampleCancel(cancelRequest);
            setOkNotice(false);
        }
    }, [okNotice]);

    return (
        <>
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
                                                value: formatDateLocal(from),
                                                operator: ">="
                                            },
                                            {
                                                table: "request",
                                                column: "create_at",
                                                value: formatDateLocal(to),
                                                operator: "<="
                                            }
                                        ]
                                    } as FilterGroup : undefined
                                )
                            }}
                        />
                        <GrPowerReset
                            className={style.resetButton}
                            onClick={handleReset}/>
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
                <section className={globalTableStyle.tableContainer}>
                    <table className={globalTableStyle.table}>
                        <thead>
                        <tr>
                            <th className={globalTableStyle.middleColumn}>Order At<br/>(YYYY-MM-DD)</th>
                            <th className={globalTableStyle.longColumn}>User Name</th>
                            <th className={globalTableStyle.middleColumn}>Institution</th>
                            <th className={globalTableStyle.longColumn}>Registration ID</th>
                            <th className={globalTableStyle.longColumn}>Service</th>
                            <th className={globalTableStyle.longColumn}>Patient(s) Name</th>
                            <th className={globalTableStyle.longColumn}>MRN</th>
                            <th className={globalTableStyle.longColumn}>Reason</th>
                            <th className={globalTableStyle.middleColumn}>Request</th>
                            <th className={globalTableStyle.middleColumn}>Cancel</th>
                            <th className={globalTableStyle.shortColumn}>Report Download</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requestData && requestData.length > 0 ? ( requestData.map((request) => (
                            <tr key={`${request!.service!.id!}${request!.sample!.id!}`}>
                                <td className={globalTableStyle.middleColumn}>{request.create_at ? formatDateLocal(new Date(request.create_at)) : ''}</td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.user?.name}/></td>
                                <td className={globalTableStyle.middleColumn}><CellTooltip text={request.sample?.patient?.organization?.name}/></td>
                                <td className={globalTableStyle.longColumn}>{request.sample?.barcode}</td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.service?.name}/></td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.sample?.patient?.name}/></td>
                                <td className={globalTableStyle.longColumn}>{request.sample?.patient?.serial}</td>
                                <td className={globalTableStyle.longColumn}>{request.lims_resample_reason}</td>
                                <td className={globalTableStyle.underlineBlue} onClick={() => handleRequestClick(request)}>Request</td>
                                <td className={globalTableStyle.underlineRed} onClick={() => handleCancelToResampleClick(request)}>Cancel</td>
                                <td className={globalTableStyle.middleColumn}>
                                    {(request.reports as Report[])
                                        ?.filter((report: Report) => report.type === 'PDF' && report.is_latest === true).map((report) => (
                                            <FontAwesomeIcon
                                                key={report.id}
                                                className={downloadStyle.downloadIcon}
                                                icon={faFilePdf}
                                                onClick={() => handleDownloadOnClick(report, request)}/>
                                        ))}
                                </td>
                            </tr>
                            ))
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
            {requestModalOpen && selectedRequest && (
                <RequestModal
                    selectedRequest={selectedRequest}
                    closeModal={closeModal}
                    refreshData={refreshData}
                />
            )}
        </>
    );
}