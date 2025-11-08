package Repository;

import Data.DataConnection;
import Model.NhanVien;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienRepository {
    
    public List<NhanVien> getAll() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY MaNhanVien DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        }
        return list;
    }
    
    public NhanVien getById(int maNhanVien) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        }
        return null;
    }
    
    public NhanVien getBySoDienThoai(String soDienThoai) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE SoDienThoai = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, soDienThoai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        }
        return null;
    }
    
    public List<NhanVien> searchByHoTen(String hoTen) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE HoTen LIKE ? ORDER BY MaNhanVien DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + hoTen + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    public List<NhanVien> getByChucVu(String chucVu) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE ChucVu = ? ORDER BY MaNhanVien DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chucVu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(NhanVien nhanVien) throws SQLException {
        String sql = "INSERT INTO NhanVien (HoTen, NgaySinh, SoDienThoai, DiaChi, ChucVu, NgayVaoLam, LuongCanBan) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setNhanVienParameters(stmt, nhanVien);
            stmt.setBigDecimal(7, nhanVien.getLuongCanBan());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        nhanVien.setMaNhanVien(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(NhanVien nhanVien) throws SQLException {
        String sql = "UPDATE NhanVien SET HoTen=?, NgaySinh=?, SoDienThoai=?, DiaChi=?, ChucVu=?, NgayVaoLam=?, LuongCanBan=? " +
                    "WHERE MaNhanVien=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setNhanVienParameters(stmt, nhanVien);
            stmt.setBigDecimal(7, nhanVien.getLuongCanBan());
            stmt.setInt(8, nhanVien.getMaNhanVien());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maNhanVien) throws SQLException {
        // Kiểm tra xem nhân viên có được tham chiếu trong các bảng khác không
        if (hasReferences(maNhanVien)) {
            throw new SQLException("Không thể xóa nhân viên vì có dữ liệu liên quan trong hệ thống");
        }
        
        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private boolean hasReferences(int maNhanVien) throws SQLException {
        // Kiểm tra các bảng có tham chiếu đến nhân viên
        String[] checkSqls = {
            "SELECT COUNT(*) FROM TaiKhoan WHERE MaNhanVien = ?",
            "SELECT COUNT(*) FROM HoaDon WHERE MaNhanVienLap = ?",
            "SELECT COUNT(*) FROM ChiTietHoaDon WHERE MaNhanVien = ?",
            "SELECT COUNT(*) FROM PhanTramDichVu WHERE MaNhanVien = ?",
            "SELECT COUNT(*) FROM ChiTietTienDichVuCuaNhanVien WHERE MaNhanVien = ?",
            "SELECT COUNT(*) FROM TinhLuongNhanVien WHERE MaNhanVien = ?",
            "SELECT COUNT(*) FROM CaLam WHERE MaNhanVien = ?",
            "SELECT COUNT(*) FROM DatLich WHERE MaNhanVienTao = ?",
            "SELECT COUNT(*) FROM DatLich_ChiTiet WHERE MaNhanVien = ?"
        };
        
        try (Connection conn = DataConnection.getConnection()) {
            for (String sql : checkSqls) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, maNhanVien);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isSoDienThoaiExists(String soDienThoai, Integer excludeMaNhanVien) throws SQLException {
        String sql;
        if (excludeMaNhanVien != null) {
            sql = "SELECT COUNT(*) FROM NhanVien WHERE SoDienThoai = ? AND MaNhanVien <> ?";
        } else {
            sql = "SELECT COUNT(*) FROM NhanVien WHERE SoDienThoai = ?";
        }
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, soDienThoai);
            if (excludeMaNhanVien != null) {
                stmt.setInt(2, excludeMaNhanVien);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<String> getAllChucVu() throws SQLException {
        List<String> chucVuList = new ArrayList<>();
        String sql = "SELECT DISTINCT ChucVu FROM NhanVien WHERE ChucVu IS NOT NULL ORDER BY ChucVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                chucVuList.add(rs.getString("ChucVu"));
            }
        }
        return chucVuList;
    }
    
    public int getTotalNhanVien() throws SQLException {
        String sql = "SELECT COUNT(*) FROM NhanVien";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<NhanVien> getNhanVienByNgayVaoLam(LocalDate fromDate, LocalDate toDate) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE NgayVaoLam BETWEEN ? AND ? ORDER BY NgayVaoLam DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fromDate));
            stmt.setDate(2, Date.valueOf(toDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    private void setNhanVienParameters(PreparedStatement stmt, NhanVien nhanVien) throws SQLException {
        stmt.setString(1, nhanVien.getHoTen());
        
        if (nhanVien.getNgaySinh() != null) {
            stmt.setDate(2, Date.valueOf(nhanVien.getNgaySinh()));
        } else {
            stmt.setNull(2, Types.DATE);
        }
        
        stmt.setString(3, nhanVien.getSoDienThoai());
        stmt.setString(4, nhanVien.getDiaChi());
        stmt.setString(5, nhanVien.getChucVu());
        
        if (nhanVien.getNgayVaoLam() != null) {
            stmt.setDate(6, Date.valueOf(nhanVien.getNgayVaoLam()));
        } else {
            stmt.setNull(6, Types.DATE);
        }
    }
    
    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getInt("MaNhanVien"));
        nv.setHoTen(rs.getString("HoTen"));
        
        Date ngaySinh = rs.getDate("NgaySinh");
        if (ngaySinh != null) {
            nv.setNgaySinh(ngaySinh.toLocalDate());
        }
        
        nv.setSoDienThoai(rs.getString("SoDienThoai"));
        nv.setDiaChi(rs.getString("DiaChi"));
        nv.setChucVu(rs.getString("ChucVu"));
        
        Date ngayVaoLam = rs.getDate("NgayVaoLam");
        if (ngayVaoLam != null) {
            nv.setNgayVaoLam(ngayVaoLam.toLocalDate());
        }
        
        nv.setLuongCanBan(rs.getBigDecimal("LuongCanBan"));
        
        return nv;
    }
}