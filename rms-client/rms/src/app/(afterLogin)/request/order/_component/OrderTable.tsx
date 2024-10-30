"use client"

import style from "@/app/(afterLogin)/request/order/_component/orderTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import {useRouter} from "next/navigation";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {postRequestOrders} from "@/app/(afterLogin)/request/order/_api/postRequestOrders";
import BarcodeButton from "@/app/(afterLogin)/request/order/_component/BarcodeButton";
import {Query} from "@/model/Query";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {Filter} from "@/model/Filter";

export interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "sample", column: "barcode", name: "Global courier" },
    { table: "organization", column: "id", name: "AirWaybill no" },
    { table: "patient", column: "name", name: "Institution" },
    { table: "patient", column: "name", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "birth_year", name: "Patient BOD" },
    { table: "patient", column: "sex", name: "Gender" },
    { table: "patient", column: "serial", name: "MRN" },
];

export default function OrderTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({asc: true, size:10, page:1});
    const router = useRouter();
    const isSelectedAll = requestData.every((row) => row.isSelected);
    const [filter, setFilter] = useState<Filter>({
        table: selectBoxOptions[0].table!,
        column: selectBoxOptions[0].column!,
        operator: "LIKE",
        value: ""
    })

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

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequestOrders(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data = await response.json();
            setRequestData(data as Request[]);
            setTotalPage(totalPage);
        }
        catch {
            setRequestData([]);
        }
    };


    const handleInfoClick = (row: RequestWithSelected) => {
        router.push(`/request/order/info?service=${row.service!.id}&sample=${row.sample!.id}&user_id=${row.sample!.patient!.organization!.user!.id}`);
    };

    const selectedRequest = requestData.filter((row) => row.isSelected);

    useEffect(() => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups: [
                {
                    ...prevSearch?.filter_groups?.[0] || {},
                    filters: [
                        filter,
                    ],
                },
            ],
        }));
    }, [filter]);

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <div className={style.container}>
            <section>
                <div className={style.buttonSection}>
                    <BarcodeButton selectRequest={selectedRequest}/>
                </div>
                <div className={style.filterContainerLeft}>
                    <SelectBox
                        width={"10vw"}
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                            setFilter(prev => ({
                                ...prev,
                                table: option.table!,
                                column: option.column!
                            }))
                        }}
                    />
                    <InputBox label={"search"} onChange={(value) => {
                        setFilter(prev => ({
                            ...prev,
                            value: value
                        }))
                    }}></InputBox>
                </div>
            </section>
            <div className={style.tableContainer}>
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
                        <th className={style.shortColumn}>Global courier</th>
                        <th className={style.shortColumn}>AirWaybill no.</th>
                        <th className={style.shortColumn}>Institution</th>
                        <th>Registration ID</th>
                        <th className={style.longColumn}>Service</th>
                        <th>Patient(s) Name</th>
                        <th>Patient BOD<br/>(DD/MM/YYYY)</th>
                        <th>Gender</th>
                        <th className={style.longColumn}>MRN</th>
                        <th>Info</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 && requestData.map((row, rowIndex) => (
                        <tr key={row.order_id! + row.service!.id + row.sample!.id}>
                            <td onClick={(e) => e.stopPropagation()}>
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
                            <td>Global courier</td>
                            <td>AirWaybill no.</td>
                            <td>{row.sample!.patient!.organization!.id}</td>
                            <td>{row.sample?.barcode}</td>
                            <td>{row.service?.name}</td>
                            <td>{row.sample?.patient?.name}</td>
                            <td>{row.sample?.patient?.birth_day}-{row.sample?.patient?.birth_month}-{row.sample?.patient?.birth_year}</td>
                            <td>{row.sample?.patient?.sex}</td>
                            <td>{row.sample?.patient?.serial}</td>
                            <td>
                                <FontAwesomeIcon
                                    icon={faFileLines}
                                    className={style.info}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleInfoClick(row);
                                    }}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
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