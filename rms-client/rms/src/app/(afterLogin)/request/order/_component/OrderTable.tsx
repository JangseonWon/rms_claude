"use client"

import style from "@/app/(afterLogin)/request/order/_component/orderTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import type {Page} from "@/model/Page";
import {format} from "date-fns";
import {useRouter} from "next/navigation";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {getRequestOrders} from "@/app/(afterLogin)/request/order/_api/getRequestOrders";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

export default function OrderTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [page, setPage] = useState<Page>({size:5, number:1});
    const router = useRouter();

    useEffect(() => {
        fetchData(page.size, page.number)
    }, [page.size, page.number]);

    const handlePageChange = (newPageNumber: number) => {
        setPage(prevPage =>({...prevPage, number: newPageNumber}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setPage((prevPage) => ({ ...prevPage, size: newSize, number: 1 }));
    };

    const fetchData = async (pageSize: number, pageNumber: number) => {
        const response = await getRequestOrders(pageSize, pageNumber);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;

        setRequestData(data as Request[]);
        setPage(prevPage => ({ ...prevPage, totalPage: totalPage }));
    };


    const handleInfoClick = (row: RequestWithSelected) => {
        router.push(`/request/order/info?order=${row.order_id}&service=${row.service!.id}&sample=${row.sample!.id}&user_id=${row.sample!.patient!.organization!.user!.id}`);
    };

    return (
        <div className={style.container}>
            <table className={style.table}>
                <thead>
                <tr>
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
                {requestData && requestData.length > 0 && requestData.map((row) => (
                    <tr key={row.order_id! + row.service!.id + row.sample!.id}>
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