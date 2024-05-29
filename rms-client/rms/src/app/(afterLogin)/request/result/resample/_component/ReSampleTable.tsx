"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/result/resample/_component/reSampleTable.module.css";
import type {Organization} from "@/model/Organization";
import type {Page} from "@/model/Page"
import {faAngleLeft, faAngleRight, faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {fetchOrganization} from "@/app/(afterLogin)/user/_api/fetchOrganization";
import {useSession} from "next-auth/react";
import type {Request} from "@/model/Request";
import {fetchFinishedOrder} from "@/app/(afterLogin)/request/result/download/_api/fetchFinishedOrder";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

export default function ReSampleTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData.every((row) => row.isSelected);
    const [page, setPage] = useState<Page>({size:5, number:1})
    const { data: session, status } = useSession();

    useEffect(() => {
        fetchData(page)
    }, [page.number, page.size]);

    const handlePageChange = (newPageNumber: number) => {
        setPage(prevPage =>({...prevPage, number: newPageNumber}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setPage((prevPage) => ({ ...prevPage, size: newSize }));
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

    const handleRequestOnClick = () => {
        alert("request");
    }
    const handleClosedOnClick = () => {
        alert("closed");
    }

    const fetchData = async (page: Page) => {
        const response = await fetchFinishedOrder(session?.user?.id, page);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;

        setRequestData(data as Request[]);
        setPage(prevPage => ({ ...prevPage, totalPage: totalPage }));
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
                    <th>Registration Number</th>
                    <th>Patient(s) Name</th>
                    <th>MRN</th>
                    <th>Resample Notice<br/>(YYYY/MM/DD)</th>
                    <th>Reason</th>
                    <th>Resample</th>
                    <th>Closed</th>
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
                        <td>{row.serial}</td>
                        <td>{row.sample?.patient?.name}</td>
                        <td>{row.sample?.patient?.serial}</td>
                        <td>{row.resample_at? new Date(row.resample_at).toLocaleDateString() : 'N/A'}</td>
                        <td>{row.sample?.resample_reason}</td>
                        <td className={style.request} onClick={handleRequestOnClick}>Request</td>
                        <td className={style.closed} onClick={handleClosedOnClick}>Closed</td>
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