"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/dashboard/dashboard/_component/table.module.css"
import type {Request} from "@/model/Request";
import {getRequests} from "@/app/(afterLogin)/request/dashboard/dashboard/_api/getRequests";
import {format} from "date-fns";
import type {Page} from "@/model/Page"
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export default function Table() {
    const [requestData, setRequestData] = useState<Request[]>([])
    const [page, setPage] = useState<Page>({size:5, number:1})

    useEffect(() => {
        fetchData(page)
    }, [page.number, page.size]);

    const handlePageChange = (newPageNumber: number) => {
        setPage(prevPage =>({...prevPage, number: newPageNumber}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setPage((prevPage) => ({ ...prevPage, size: newSize })); // Reset page number when size changes
    };

    const fetchData = async (page: Page) => {
        const response = await getRequests(page);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const data = await response.json();
        setRequestData(data as Request[]);
        setPage(prevPage => ({ ...prevPage, totalPage: totalPage }));
    };

    return (
        <div className={style.container}>
            <table className={style.table}>
                <thead>
                <tr>
                    <th>Registration ID</th>
                    <th>User Name</th>
                    <th>Institution</th>
                    <th>Service</th>
                    <th>Patient(s) Name</th>
                    <th>MRN</th>
                    <th>Patient BOD</th>
                    <th>Current Status</th>
                    <th>Order Date</th>
                </tr>
                </thead>
                <tbody>
                {requestData.map((row) => (
                    <tr>
                        <td>{row.sample.barcode}</td>
                        <td>{row.sample.patient.organization.user.id}</td>
                        <td>{row.sample.patient.organization.id}</td>
                        <td>{row.service.name}</td>
                        <td>{row.sample.patient.name}</td>
                        <td>{row.sample.patient.serial}</td>
                        <td>{row.sample.patient.birth_year}-{row.sample.patient.birth_month}-{row.sample.patient.birth_day}</td>
                        <td>{row.status}</td>
                        <td>{row.create_at ? format(new Date(row.create_at), "yyyy-MM-dd") : '-'}</td>
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