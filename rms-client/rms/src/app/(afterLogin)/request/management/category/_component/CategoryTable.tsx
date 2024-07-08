"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/category/_component/categoryTable.module.css";
import {Categories} from "@/model/Categories";
import {getCategories} from "@/app/(afterLogin)/request/management/category/_api/getCategories";

export default function CategoryTable() {
    const [categoryData, setCategoryData] = useState<Categories[]>([]);

    const fetchData = async () => {
        try {
            const response = await getCategories()
            const data = await response.json();
            setCategoryData(data as Categories[]);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setCategoryData([]);
        }
    }

    useEffect(() => {
        fetchData()
    }, []);

    const handleAlisSyncButtonClick = () => {
        alert('add click');
    }

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <button className={style.alisSync} onClick={handleAlisSyncButtonClick}>
                        Alis-Sync
                    </button>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                    </tr>
                    </thead>
                    <tbody>
                    {categoryData && categoryData.length > 0 && categoryData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.name}</td>
                            <td>{row.order_type}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
        </>
    );
}