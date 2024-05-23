"use client"

import style from "@/app/(afterLogin)/request/cart/_component/table.module.css"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import type {Page} from "@/model/Page";
import {getRequests} from "@/app/(afterLogin)/request/cart/_api/getRequests";
import {format} from "date-fns";
import {useRouter} from "next/navigation";

interface RequestWithSelected extends Request {
    isSelected?: boolean; // Add an optional property for selection state
}

export default function Table() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([])
    const [page, setPage] = useState<Page>({size:10, number:1})
    const router = useRouter();
    const isSelectedAll = requestData.every((row) => row.isSelected); // Check if all or none are selected

    useEffect(() => {
        fetchData(page)
    }, [page.number, page.size]);

    const fetchData = async (page: Page) => {
        const response = await getRequests(page);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const data = await response.json();
        setRequestData(data as Request[]);
        setPage(prevPage => ({ ...prevPage, totalPage: totalPage }));
    };
    const handlePageChange = (newPageNumber: number) => {
        setPage(prevPage =>({...prevPage, number: newPageNumber}));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setPage((prevPage) => ({ ...prevPage, size: newSize })); // Reset page number when size changes
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
    const handleRowClick = (row: RequestWithSelected) => {
        router.push(`/request/cart/info?order=${row.order_id}&service=${row.service!.id}&sample=${row.sample!.id}&user_id=${row.sample!.patient!.organization!.user!.id}`);
    };

    return (
        <div className={style.container}>
            <div>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>
                            <input
                                type="checkbox"
                                checked={isSelectedAll}
                                onChange={() => handleSelectAll(!isSelectedAll)}
                                className={style.checkbox}
                            />
                        </th>
                        <th>Service Name</th>
                        <th>Patient(s) Name</th>
                        <th>Patient BOD<br/>(DD/MM/YYYY)</th>
                        <th>Gender</th>
                        <th>Physician Name</th>
                        <th>Collection Date<br/>(DD/MM/YYYY)</th>
                        <th>MRN</th>
                        <th>Service Code</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData.map((row, rowIndex) => (
                        <tr
                            key={row.order_id! + row.service!.id + row.sample!.id}
                            onClick={()=>handleRowClick(row)}
                        >
                            <td onClick={(e) => e.stopPropagation()}>
                                <input
                                    type="checkbox"
                                    checked={row.isSelected || false}
                                    onChange={() => handleSelectChange(rowIndex, !row.isSelected)}
                                    className={style.checkbox}
                                />
                            </td>
                            <td>{row.service?.name}</td>
                            <td>{row.sample?.patient?.name}</td>
                            <td>{row.sample?.patient?.birth_day}-{row.sample?.patient?.birth_month}-{row.sample?.patient?.birth_year}</td>
                            <td>{row.sample?.patient?.sex}</td>
                            <td>{row.physician}</td>
                            <td>{row.sample?.sampling_on ? format(new Date(row.sample.sampling_on), "dd-MM-yyyy") : '-'}</td>
                            <td>{row.sample?.patient?.serial}</td>
                            <td>{row.service?.id}</td>
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
        </div>
    )
}