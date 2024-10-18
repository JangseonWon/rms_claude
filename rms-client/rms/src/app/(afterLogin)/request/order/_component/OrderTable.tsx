"use client"

import style from "@/app/(afterLogin)/request/order/_component/orderTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import {format} from "date-fns";
import {useRouter} from "next/navigation";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {postRequestOrders} from "@/app/(afterLogin)/request/order/_api/postRequestOrders";
import BarcodeButton from "@/app/(afterLogin)/request/order/_component/BarcodeButton";
import {Query} from "@/model/Query";
import {SelectBoxOption} from "@/model/SelectBoxOption";

export interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

export default function OrderTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: true, size:10, page:1});
    const router = useRouter();
    const isSelectedAll = requestData.every((row) => row.isSelected);

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

    const handleSearchChange = (option: SelectBoxOption, value: string) => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups:[
                {
                    condition_type: "OR",
                    filters: [
                        {
                            table: option.table!,
                            column: option.column!,
                            value: value,
                            operator: "LIKE"
                        }
                    ]
                }
            ],
            page:1
        }));
    };

    const fetchData = async (search: Query) => {
        const response = await postRequestOrders(search);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const data = await response.json();
        setRequestData(data as Request[]);
        setTotalPage(totalPage);
    };


    const handleInfoClick = (row: RequestWithSelected) => {
        router.push(`/request/order/info?service=${row.service!.id}&sample=${row.sample!.id}&user_id=${row.sample!.patient!.organization!.user!.id}`);
    };

    const selectedRequest = requestData.filter((row) => row.isSelected);

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <div className={style.container}>
            <BarcodeButton selectRequest={selectedRequest}/>
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
                    <th>Service Name</th>
                    <th>Patient(s) Name</th>
                    <th>Patient BOD<br/>(DD/MM/YYYY)</th>
                    <th>Gender</th>
                    <th>Physician Name</th>
                    <th>Collection Date<br/>(DD/MM/YYYY)</th>
                    <th>MRN</th>
                    <th>Service Code</th>
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
                        <td>{row.service?.name}</td>
                        <td>{row.sample?.patient?.name}</td>
                        <td>{row.sample?.patient?.birth_day}-{row.sample?.patient?.birth_month}-{row.sample?.patient?.birth_year}</td>
                        <td>{row.sample?.patient?.sex}</td>
                        <td>{row.physician}</td>
                        <td>{row.sample?.sampling_on ? format(new Date(row.sample.sampling_on), "dd-MM-yyyy") : '-'}</td>
                        <td>{row.sample?.patient?.serial}</td>
                        <td>{row.service?.id}</td>
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
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}