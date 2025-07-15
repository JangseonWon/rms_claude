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
import CellTooltip from "@/app/_component/CellToolTip";
import {GrPowerReset} from "react-icons/gr";
import DownloadRequestExcelButton
    from "@/app/(afterLogin)/request/management/request/_component/DownloadRequestExcelButton";
import RequestDetailInfo from "@/app/_component/RequestDetailInfo";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {useConfirmDialog} from "@/app/_component/dialog/useConfirmDialog";
import GreenButton from "@/app/_component/GreenButton";
import {cancelRequests} from "@/app/(afterLogin)/request/management/request/_api/cancelRequests";

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
    { table: "request", column: "awb_number", name: "운송번호" }
];

const notCartFilter: Filter = {
    table: "request",
    column: "status",
    value: Status.CART.valueOf(),
    operator: "!="
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
            filters: [
                notCartFilter,
                notCancelFilter
            ]
        }
    ],
    size:10,
    page:1
}

export default function RequestManageTable() {
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [requests, setRequests] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requests && requests.length > 0 ? requests.every((row) => row.isSelected) : false;
    const [isCancelledChecked, setIsCancelledChecked] = useState(false);
    const [infoRequest, setInfoRequest] = useState<Request>();
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [fromDate, setFromDate] = useState<Date | null>(null);
    const [toDate, setToDate] = useState<Date | null>(null);
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const showAlert = CallAlertDialog();
    const {confirm, dialogComponent } = useConfirmDialog();

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
                            notCartFilter
                        ]
                    },
                    ...(prevSearch.filter_groups || []).filter(group => group.filters?.some(filter => filter.column === "create_at"))
                ],
                page: 1
            };
        });
    };

    const handleCancelCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const checked = e.target.checked;
        setIsCancelledChecked(checked);
        handleSearchChange(
            { table: "request", column: "is_cancel" },
            checked.toString()
        );
    };

    const handleReset = () => {
        setFromDate(null);
        setToDate(null);
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

    const handleInfoClick = (row: RequestWithSelected) => {
        setInfoRequest(row);
        setModalOpen(true);
    };

    const handleSelectAll = (isSelected: boolean) => {
        setRequests((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
    };

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequests((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };

    const closeModal = () => {
        setInfoRequest(undefined);
        setModalOpen(false);
    }

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

    const handleCancelClick = async() => {
        const selectedRequests = requests.filter(request => request.isSelected);
        if( selectedRequests.length === 0) {
            showAlert("No selected.");
            return;
        }
        const ok = await confirm("Confirmation","Are you sure you want to cancel this request? \n This action cannot be undone.")
        if (ok) {
            const response = await cancelRequests(selectedRequests)
            await fetchData(search)
            if (response) showAlert("Success");
            else showAlert("Fail");
        }
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <div>
            {dialogComponent}
            <div className={globalTableStyle.container}>
                <div className={globalTableStyle.formGroupRight}>
                    <GreenButton name={'Cancel'} onClick={handleCancelClick}/>
                    <DownloadRequestExcelButton
                        search={(() => {
                            const {page, size, ...rest} = search;
                            return rest;
                        })()}
                    />
                </div>
                <div className={globalTableStyle.formGroupRight}>
                    <label>Show Canceled</label>
                    <label form="agree" className={globalTableStyle.checkbox}>
                        <input
                            type="checkbox"
                            checked={isCancelledChecked}
                            onChange={handleCancelCheckboxChange}
                            className={globalTableStyle.checkbox}
                        />
                        <span className={globalTableStyle.checkmark}></span>
                    </label>
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
                            maxMonthsRange={3}
                        />
                        <GrPowerReset
                            className={globalTableStyle.resetButton}
                            onClick={handleReset}
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
                        <th>의뢰날짜<br/>(YYYY-MM-DD)</th>
                        <th>거래처명</th>
                        <th>의뢰기관</th>
                        <th>의뢰번호</th>
                        <th>환자명</th>
                        <th>생년월일<br/>(YYYY-MM-DD)</th>
                        <th>성별</th>
                        <th>담당의</th>
                        <th>검체채취일<br/>(YYYY-MM-DD)</th>
                        <th>의뢰명</th>
                        <th>MRN</th>
                        <th>의뢰코드</th>
                        <th>배송업체</th>
                        <th>운송번호</th>
                        <th>상태</th>
                        <th>취소된의뢰</th>
                        <th>취소시간</th>
                        <th>림스재검사유</th>
                        <th>림스재검요청시간</th>
                        <th>상세정보</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requests && requests.length > 0 && requests.map((request, rowIndex) => (
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
                            <td className={globalTableStyle.longColumn}><CellTooltip text={request.user?.name}/></td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip
                                text={request.sample?.patient?.organization?.name}/></td>
                            <td className={globalTableStyle.longColumn}><CellTooltip text={request.sample?.barcode}/>
                            </td>
                            <td className={globalTableStyle.longColumn}><CellTooltip
                                text={request.sample?.patient?.name}/></td>
                            <td className={globalTableStyle.middleColumn}>{getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}</td>
                            <td className={globalTableStyle.shortColumn}>{request.sample?.patient?.sex}</td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip text={request.physician}/></td>
                            <td className={globalTableStyle.middleColumn}>{request.sample?.sampling_on ? formatDateLocal(new Date(request.sample.sampling_on)) : '-'}</td>
                            <td className={globalTableStyle.longColumn}><CellTooltip text={request.service?.name}/></td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip
                                text={request.sample?.patient?.serial}/></td>
                            <td className={globalTableStyle.shortColumn}><CellTooltip text={request.service?.id}/></td>
                            <td className={globalTableStyle.shortColumn}><CellTooltip text={request.courier_company}/>
                            </td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip text={request.awb_number}/></td>
                            <td className={globalTableStyle.longColumn}><CellTooltip text={
                                request.status === Status.UNCONFIRMED_ORDER ? 'PENDING_APPROVAL' :
                                    request.status === Status.COMPLETED_ORDER ? 'APPROVAL' :
                                        request.status
                            }/></td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip
                                text={request.is_cancel ? "취소된의뢰" : ""}/></td>
                            <td className={globalTableStyle.longColumn}>
                                {request.is_cancel_at ? format(new Date(request.is_cancel_at), 'yyyy-MM-dd HH:mm:ss') : ''}
                            </td>
                            <td className={globalTableStyle.textColumn}>{request.lims_resample_reason}</td>
                            <td className={globalTableStyle.longColumn}>
                                {request.lims_resample_at ? format(new Date(request.lims_resample_at), 'yyyy-MM-dd HH:mm:ss') : ''}
                            </td>
                            <td className={globalTableStyle.shortColumn}>
                                <FontAwesomeIcon
                                    icon={faFileLines}
                                    className={globalTableStyle.info}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleInfoClick(request)
                                    }}/>
                            </td>
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
            {modalOpen && infoRequest && (
                <RequestDetailInfo
                    editable={true}
                    selectedRequest={infoRequest}
                    closeModal={closeModal}
                />
            )}
        </div>
    );
}