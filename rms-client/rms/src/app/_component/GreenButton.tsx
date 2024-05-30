"use client"

import style from "@/app/_component/greenButton.module.css"

type Props = {
    name: string
    onClick?: () => void
    disabled?: boolean
}
export default function GreenButton({name, onClick, disabled=false}: Props) {
    return (
        <button className={`${style.greenButton} ${disabled ? style.disable : ""}`} onClick={disabled ? undefined : onClick}>{name}</button>
    )
}