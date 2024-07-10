"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "@/app/(afterLogin)/user/_component/institutionTable.module.css";
import type {Organization} from "@/model/Organization";
import type {Page} from "@/model/Page"
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import EditModal from "@/app/(afterLogin)/user/_component/EditModal";
import {fetchOrganization} from "@/app/(afterLogin)/user/_api/fetchOrganization";
import {useSession} from "next-auth/react";

export default function InstitutionTable() {
    const [organizationData, setOrganizationData] = useState<Organization[]>([])
    const [page, setPage] = useState<Page>({size:5, number:1})
    const { data: session, status } = useSession();

    const handlePageChange = (newPageNumber: number) => {
        setPage(prevPage =>({...prevPage, number: newPageNumber}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setPage((prevPage) => ({ ...prevPage, size: newSize }));
    };

    const fetchData = useCallback(async (pageSize: number, pageNumber: number) => {
        const response = await fetchOrganization(session?.user?.id, pageSize, pageNumber);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;

        setOrganizationData(data as Organization[]);
        setPage(prevPage => ({ ...prevPage, totalPage: totalPage }));
    }, [session?.user?.id]);

    useEffect(() => {
        fetchData(page.size, page.number)
    }, [page.size, page.number, fetchData]);

    return (
        <div className={style.container}>
            <table className={style.table}>
                <thead>
                <tr>
                    <th>Institute / Practice Name</th>
                    <th>Type</th>
                    <th>Registration Number</th>
                    <th>Nursing Number</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {organizationData && organizationData.length > 0 && organizationData.map((row) => (
                    <tr key={row.id + row.name! + row.type + row.user}>
                        <td>{row.name}</td>
                        <td>{row.type}</td>
                        <td>{row.registration_number}</td>
                        <td>{row.nursing_number}</td>
                        <td>
                            <EditModal id={row.id}/>
                        </td>
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
    );
}