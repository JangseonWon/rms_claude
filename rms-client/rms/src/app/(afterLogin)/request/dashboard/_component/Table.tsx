"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/dashboard/_component/table.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import type {Request} from "@/model/Request";
import {postRequests} from "@/app/(afterLogin)/request/dashboard/_api/postRequests";
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
import CellTooltip from "@/app/_component/CellToolTip";
import {formatDateLocal, getStringDateFromComponents} from "@/app/_component/DateUtil";
import RequestDetailInfo from "@/app/_component/RequestDetailInfo";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

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
    const [infoRequest, setInfoRequest] = useState<Request>();
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const status = useStatus();
    const setStatus = useSetStatus();
    const selectBoxOptions: SelectBoxOption[] = [
        { table: "sample", column: "barcode", name: "Registration ID" },
        { table: "organization", column: "id", name: "Institution" },
        { table: "patient", column: "name", name: "Patient(s) Name" },
        { table: "service", column: "name", name: "Service" },
        { table: "patient", column: "serial", name: "MRN" },
        { table: "patient", column: "birth_year", name: "Patient BOD" },
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
                    }
                ],
                page: 1
            };
        });
    };

    const handleInfoClick = (row: RequestWithSelected) => {
        setInfoRequest(row);
        setModalOpen(true);
    };

    const closeModal = () => {
        setInfoRequest(undefined);
        setModalOpen(false);
    }

    const handleReset = () => {
        setSearch({ sort_by: "create_at", asc: false, size: 5, page: 1 });
        setSearchValue('');
        setStatus(Status.TOTAL);
        setSelectOption({ table: "sample", column: "id", name: "Registration ID" });
        addDateFilter(null , null);
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
                <div className={style.filterContainerLeft}>
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) =>{
                            addDateFilter(from, to);
                        }}/>
                    <GrPowerReset
                        className={style.resetButton}
                        onClick={handleReset}/>
                </div>
                <div className={style.filterContainerRight}>
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
                    <th className={globalTableStyle.middleColumn}>Order Date<br/>(YYYY-MM-DD)</th>
                    <th className={globalTableStyle.middleColumn}>Registration ID</th>
                    <th className={globalTableStyle.middleColumn}>User Name</th>
                    <th className={globalTableStyle.shortColumn}>Institution</th>
                    <th className={globalTableStyle.shortColumn}>Service</th>
                    <th className={globalTableStyle.middleColumn}>Patient(s) Name</th>
                    <th className={globalTableStyle.shortColumn}>MRN</th>
                    <th className={globalTableStyle.middleColumn}>Patient BOD<br/>(YYYY-MM-DD)</th>
                    <th className={globalTableStyle.middleColumn}>Current Status</th>
                    <th className={globalTableStyle.shortColumn}>Info</th>
                </tr>
                </thead>
                <tbody>
                {requestData && requestData.length > 0 ? ( requestData.map((request) => (
                        <tr key={`${request.service!.id}${request.sample!.id}`}>
                            <td className={globalTableStyle.shortColumn}><CellTooltip
                                text={request.create_at ? formatDateLocal(new Date(request.create_at)) : '-'}/></td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip text={request.sample!.barcode}/>
                            </td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip
                                text={request.sample!.patient!.organization!.user!.name}/></td>
                            <td className={globalTableStyle.shortColumn}><CellTooltip
                                text={request.sample!.patient!.organization!.id}/></td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip text={request.service!.name}/>
                            </td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip
                                text={request.sample!.patient!.name}/></td>
                            <td className={globalTableStyle.middleColumn}><CellTooltip
                                text={request.sample!.patient!.serial}/></td>
                            <td className={globalTableStyle.shortColumn}><CellTooltip text={request.sample?.patient ?
                                getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day) : '-'}/>
                            </td>
                            <td className={globalTableStyle.shortColumn}>
                                <CellTooltip text={
                                    request.status === 'UNCONFIRMED_ORDER' ? 'Pending Approval' :
                                        request.status === 'COMPLETED_ORDER' ? 'Approval' :
                                            request.status
                                }/>
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
                    ))
                ) : (
                    <tr>
                        <td colSpan={10} className={style.noData}>
                            The searched data does not exist
                        </td>
                    </tr>
                )}
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
            {modalOpen && (
                <RequestDetailInfo
                    disabled={true}
                    serviceId={infoRequest?.service!.id!}
                    sampleId={infoRequest?.sample!.id!}
                    requestGroupId = {infoRequest?.request_group!.id!}
                    userId={infoRequest?.user!.id!}
                    closeModal={closeModal}
                />
            )}
        </div>
    )
}