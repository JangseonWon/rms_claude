"use client"

import style from "@/app/(afterLogin)/request/order/_component/orderTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import type {Page} from "@/model/Page";
import {format} from "date-fns";
import {useRouter} from "next/navigation";
import {getRequestOrders} from "@/app/(afterLogin)/request/order/_api/getRequestOrders";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

export default function OrderTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [page, setPage] = useState<Page>({size:5, number:1});
    const router = useRouter();
    const isSelectedAll = requestData.every((row) => row.isSelected);

    useEffect(() => {
        fetchData(page)
    }, [page.number, page.size]);

    const handlePageChange = (newPageNumber: number) => {
        setPage(prevPage =>({...prevPage, number: newPageNumber}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setPage((prevPage) => ({ ...prevPage, size: newSize, number: 1 }));
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

    const handleInfoOnclick = () => {
        alert("test");
    }

    const fetchData = async (page: Page) => {
        const response = await getRequestOrders(page);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;

        setRequestData(data as Request[]);
        setPage(prevPage => ({ ...prevPage, totalPage: totalPage }));
    };


    const handleRowClick = (row: RequestWithSelected) => {
        router.push(`/request/cart/info?order=${row.order_id}&service=${row.service!.id}&sample=${row.sample!.id}&user_id=${row.sample!.patient!.organization!.user!.id}`);
    };

    return (
        <div className={style.container}>
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
                {requestData.map((row, rowIndex) => (
                    <tr>
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
                        <td>{row.service?.name}</td>
                        <td>{row.sample?.patient?.name}</td>
                        <td>{row.sample?.patient?.birth_day}-{row.sample?.patient?.birth_month}-{row.sample?.patient?.birth_year}</td>
                        <td>{row.sample?.patient?.sex}</td>
                        <td>{row.physician}</td>
                        <td>{row.sample?.sampling_on ? format(new Date(row.sample.sampling_on), "dd-MM-yyyy") : '-'}</td>
                        <td>{row.sample?.patient?.serial}</td>
                        <td>{row.service?.id}</td>
                        <td>
                            <FontAwesomeIcon icon={faFileLines} className={style.info} onClick={handleInfoOnclick}/>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className={style.pagination}>
                <span>items per page:</span>
                <div className={style.select}>
                    <select onChange={handlePageSizeChange} defaultValue={page.size}>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                    </select>
                </div>
                <span> 1-{page.totalPage} of {page.number} </span>
                <button
                    disabled={page.number === 1}
                    onClick={() => handlePageChange(page.number - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={page.number === page.totalPage}
                    onClick={() => handlePageChange(page.number + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    )
}