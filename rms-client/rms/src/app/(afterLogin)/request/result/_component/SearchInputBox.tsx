'use client';

import InputBox from "@/app/_component/InputBox";
import style from "@/app/(afterLogin)/request/result/_component/searchInputBox.module.css";
import React from "react";

export default function SearchInputBox() {
    return (
        <div className={style.container}>
            <div className={style.searchInput}>
            <InputBox
                    label={"Search"}
                />
            </div>
        </div>
    );
}