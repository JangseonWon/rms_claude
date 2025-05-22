"use client"

import globalTableStyle from "@/css/globalTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {postRequests} from "@/app/(afterLogin)/request/order/_api/postRequests";
import {FilterGroup, Query} from "@/model/Query";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {Filter} from "@/model/Filter";
import {Status} from "@/model/Status";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import BlueButton from "@/app/_component/BlueButton";
import GreenButton from "@/app/_component/GreenButton";
import AirWaybillModal from "@/app/(afterLogin)/request/order/confirm/_component/AirWaybillModal";
import {patchRequests} from "@/app/(afterLogin)/request/order/_api/patchRequests";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import CellTooltip from "@/app/_component/CellToolTip";
import style from "@/css/qna/qnaTable.module.css";
import {GrPowerReset} from "react-icons/gr";
import {formatDateLocal, getStringDateFromComponents} from "@/app/_component/DateUtil";
import RequestDetailInfo from "@/app/_component/RequestDetailInfo";
import {deleteRequests} from "@/app/(afterLogin)/request/order/_api/deleteRequests";
import {useConfirmDialog} from "@/app/_component/dialog/useConfirmDialog";
import {format} from "date-fns";


export interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "request", column: "courier_company", name: "Global courier"},
    { table: "request", column: "awb_number", name: "AirWaybill no"},
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" },
];
const defaultFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.UNCONFIRMED_ORDER.valueOf()
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
    ]
}

export default function RequestTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [airWaybillModal, setAirWaybillModal] = useState<boolean>(false);
    const [selectedRequests, setSelectedRequests] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData.every((row) => row.isSelected);
    const [infoModalOpen, setInfoModalOpen] = useState<boolean>(false);
    const [infoRequest, setInfoRequest] = useState<Request>();
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [fromDate, setFromDate] = useState<Date | null>(null);
    const [toDate, setToDate] = useState<Date | null>(null);
    const showAlert = CallAlertDialog();
    const {confirm, dialogComponent } = useConfirmDialog()

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };
    const handleDeleteClick = async() => {
        const selectedRequests = requestData.filter(request => request.isSelected);
        if( selectedRequests.length === 0) {
            showAlert("No selected.");
            return;
        }
        const ok = await confirm("Confirmation","Are you sure you want to delete this request? \n This action cannot be undone.")
        if (ok) {
            const response = await deleteRequests(selectedRequests)
            await fetchData(search)
            if (response) showAlert("Success")
        }
    }
    const handleAirWaybillClick = () => {
        const selected = requestData.filter((request) => request.isSelected);
        if(selected.length == 0) {
            showAlert("No selected.")
            return
        }
        setSelectedRequests(selected);
        setAirWaybillModal(true);
    }

    const handleConfirmClick = async () => {
        const ok = await confirm("Confirmation","Are you sure you want to confirm?")
        if(ok){
            const selected = requestData.filter((request) => request.isSelected);
            if(selected.length == 0) {
                showAlert("No selected.")
                return
            }
            const hasMissingInfo = selected.some(
                (request) => !request.awb_number || !request.courier_company
            );
            if (hasMissingInfo) {
                showAlert("Air Waybill information is incomplete.");
                return;
            }
            const updatedRequests = selected.map((request) => ({
                sample: { id: request.sample?.id },
                service: { id: request.service?.id },
                status: Status.COMPLETED_ORDER.valueOf(),
            }));

            const response = await patchRequests(updatedRequests);
            if(response.ok) {
                showAlert("Success")
                await fetchData(search);
            } else {
                console.error("Failed to confirm requests:", response.statusText);
            }
        }
    };

    const closeModal = () => {
        setInfoModalOpen(false);
        setAirWaybillModal(false);
    }

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) => prevData.map((row) => ({ ...row, isSelected })));
    };

    const handleInfoClick = (request: RequestWithSelected) => {
        setInfoRequest(request);
        setInfoModalOpen(true);
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
                ]
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
                ]
            };
        });
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
            };
        });
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search);
            const data = await response.json();
            setRequestData(data as Request[]);
        }
        catch {
            setRequestData([]);
        }
    };

    useEffect(() => {
        fetchData(search);
    }, [search]);

    return (
        <>
            {dialogComponent}
            <div className={globalTableStyle.formGroupRight}>
                <GreenButton name={'Delete'} onClick={handleDeleteClick}/>
                <GreenButton name={'AirWaybill'} onClick={handleAirWaybillClick}/>
                <BlueButton name={'Confirm'} onClick={handleConfirmClick}/>
            </div>
            <div className={globalTableStyle.formGroupBetween}>
                <div style={{ position: "relative", zIndex: 3 }}>
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
                        onClick={handleReset}/>
                </div>
                <div>
                    <SelectBox
                        width={"200px"}
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                        }}
                    />
                    <InputBox
                        label={"search"}
                        onChange={(value) => {
                            handleSearchChange(selectedOption, value)
                    }}></InputBox>
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
                        <th className={`${globalTableStyle.longColumn} ${globalTableStyle.stickyColumnHeader}`}>Global courier</th>
                        <th className={`${globalTableStyle.longColumn} ${globalTableStyle.stickyColumnHeaderSecond} ${globalTableStyle.stickyColumnLast}`}>AirWaybill no.</th>
                        <th className={globalTableStyle.middleColumn}>Order Date<br/>(YYYY-MM-DD)</th>
                        <th className={globalTableStyle.middleColumn}>Resample</th>
                        <th className={globalTableStyle.longColumn}>User Name</th>
                        <th className={globalTableStyle.middleColumn}>Institution</th>
                        <th className={globalTableStyle.longColumn}>Registration ID</th>
                        <th className={globalTableStyle.longColumn}>Service</th>
                        <th className={globalTableStyle.longColumn}>Patient(s) Name</th>
                        <th className={globalTableStyle.middleColumn}>Patient(s) DOB<br/>(YYYY-MM-DD)</th>
                        <th className={globalTableStyle.longColumn}>MRN</th>
                        <th className={globalTableStyle.shortColumn}>Info</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 ? (requestData.map((request, rowIndex) => (
                            <tr key={`${request.service!.id}${request.sample!.id}`}>
                                <td className={globalTableStyle.stickyColumnCheckBox} onClick={(e) => e.stopPropagation()}>
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
                                <td className={`${globalTableStyle.longColumn} ${globalTableStyle.stickyColumnFirstColumn}`}>{request.courier_company}</td>
                                <td className={`${globalTableStyle.longColumn} ${globalTableStyle.stickyColumnSecondColumn} ${globalTableStyle.stickyColumnLast}`}>{request.awb_number}</td>
                                <td className={globalTableStyle.middleColumn}>{request.create_at ? formatDateLocal(new Date(request.create_at)) : ''}</td>
                                <td className={globalTableStyle.middleColumn}>{request.request_relation?.id == 2 && request.request_relation.name}</td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.user?.name}/></td>
                                <td className={globalTableStyle.middleColumn}><CellTooltip text={request.sample?.patient?.organization?.name}/></td>
                                <td className={globalTableStyle.longColumn}>{request.sample?.barcode}</td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.service?.name}/></td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.sample?.patient?.name}/></td>
                                <td className={globalTableStyle.middleColumn}>{getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}</td>
                                <td className={globalTableStyle.longColumn}>{request.sample?.patient?.serial}</td>
                                <td className={globalTableStyle.shortColumn}>
                                    <FontAwesomeIcon
                                        icon={faFileLines}
                                        className={globalTableStyle.info}
                                        onClick={(e) => {
                                            handleInfoClick(request)
                                            e.stopPropagation();
                                        }}/>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                        <td colSpan={10} className={globalTableStyle.noData}>
                                The searched data does not exist
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
            {infoModalOpen && (
                <RequestDetailInfo
                    disabled={true}
                    serviceId={infoRequest?.service!.id!}
                    sampleId={infoRequest?.sample!.id!}
                    requestGroupId = {infoRequest?.request_group!.id!}
                    userId={infoRequest?.user!.id!}
                    closeModal={closeModal}
                />
            )}
            {airWaybillModal && (
                <AirWaybillModal
                    closeModal={closeModal}
                    selectedRequests={selectedRequests}
                    onConfirm={async (courierCompany, awbNumber) => {
                        const updatedRequests: Request[] = selectedRequests.map((request) => ({
                            sample: {id: request.sample?.id},
                            service: {id: request.service?.id},
                            courier_company: courierCompany,
                            awb_number: awbNumber,
                        }));
                        const response = await patchRequests(updatedRequests);
                        closeModal();

                        if (response.ok) {
                            showAlert("Success");
                            closeModal();
                            await fetchData(search);
                        } else {
                            showAlert("Fail");
                        }
                    }}
                />
            )}
        </>
    )
}